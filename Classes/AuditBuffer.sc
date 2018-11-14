AuditBuffer {
	var <server;
	var <buffer; //TEMP getter
	var <mirFile;//TEMP getter
	var <mirFilepath;
	var <features;//TEMP getter [AuditFeatureData]
	var <soundFile;//TEMP getter
	var samples;//sclang sample values from buffer
	var frames;

	*new{arg server;
		^super.new.initAuditBuffer(server);
	}

	initAuditBuffer{arg server_;
		server = server_ ? Server.default;
	}

	free{arg action;
		if(soundFile.notNil, {
			if(soundFile.isOpen, { soundFile.close; });
		});

		if(buffer.notNil, {
			buffer.free(action);
		});
	}

	duration{
		^buffer.duration;
	}

	bufnum {
		^buffer.bufnum;
	}

	findQualifiedSegmentIndexes{arg ...criterions;
		var result = Set.new;
		//each criterion is for a declared feature name
		criterions.do({arg criterion, i;
			var featureData;
			if(criterion.isKindOf(Symbol), {
				featureData = features[criterion];
			}, {
				if(criterion.isKindOf(AuditAnalysisArgs), {
					//check if there is one that have matching
					//analysis args.
					featureData = features.values.detect({arg it;
						it == criterion});
				});
			});
			if(featureData.isNil, {
				Error("Did not find feature
					data for criterion arg: '%'".format(criterion)
				).throw;
			});
			result.addAll(
				featureData.findQualifiedSegmentIndexes(criterion)
			);
		});
		^result.asArray.sort;
	}

	getStartTimes{arg indexes;
		^mirFile.frameStartTimes.atAll(indexes);
	}

	findQualifiedSegments{arg criterions;
		var result;
		result = this.findQualifiedSegmentIndexes(criterions);
		result = result.clumpConsecutive;
		result = result.collect({arg segment;
			AuditBufferSegment(segment.first, segment.size, buffer);
		});
		^result;
	}

	analyze{arg featureArgsList, action;
		fork{
			var analysisArgs;
			var pathName = PathName(soundFile.path);
			analysisArgs = featureArgsList ? [
				[\Loudness],
				[\SensoryDissonance],
				[\SpecCentroid],
				[\SpecPcile, 0.90],
				[\SpectralEntropy],
				[\SpecFlatness],
				[\Onsets],
				[\FFTSlope],
				[\FFTCrest],
				[\FFTSpread],
				[\Tartini, 2],
				[\KeyClarity],
				[\MFCC],
				[\Chromagram]
			];
			mirFile = SCMIRAudioFile(pathName.fullPath, analysisArgs);

			//"Starting analysis of '%'".format(
			//	pathName.fullPath
			//).postln;
			mirFile.extractFeatures();
			mirFile.extractOnsets();
			//"\tAnalysis DONE".postln;

			mirFilepath = "%%.scmirZ".format(
				pathName.pathOnly,
				pathName.fileNameWithoutExtension
			);
			mirFile.save(mirFilepath);
			features = this.class.prMakeFeatures(mirFile, analysisArgs);
			//"features done: %".format(features).postln;
			action.value(mirFile);
		}
	}

	*prMakeFeatures{arg mirFile;
		var result;
		var idxInfo;
		"Making features".postln;
		idxInfo = mirFile.featureinfo.collect(_.first);
		idxInfo = idxInfo +++ mirFile.resolveFeatureNumbers;
		result = IdentityDictionary.new;
		idxInfo.do{arg item, i;
			var featureName, startIndex, numItems;
			var featureObj, featureData;
			#featureName, startIndex, numItems = item;
			//when SCMIR generates default args for some features,
			//e.g. Chromagram the keys is changed to an instance of a
			//Class. We change it to a symbol here, leaving that 'bug'
			//in scmir alone.
			if(featureName.isKindOf(Class), {
				featureName = featureName.asSymbol;
			});
			"featureName: %[%], startIndex: %, numItems: %".format(
				featureName, featureName.class, startIndex, numItems
			).postln;


			try{
				var analysisArgs;
				if(mirFile.featureinfo[i].size > 1, {
					analysisArgs = mirFile.featureinfo[i][1..];
				});
				featureObj = AuditFeatureData.newFromMirFile(
					featureName,
					analysisArgs,
					mirFile,
					startIndex,
					numItems
				)
			} {|err|
				"Failed to make AuditFeature obj for '%'".format(
					featureName
				).warn;
				err.throw;
			};
			result.put( featureName, featureObj);
		};
		^result;
	}

	frames{
		if(frames.isNil, {
			frames = this.class.prGetSoundFileSamples(soundFile);
		});
		^frames;
	}

	//Load the frames from the soundFile object
	*prGetSoundFileSamples{arg sf;
		var result;
		result = FloatArray.newClear(sf.numFrames * sf.numChannels);
		sf.readData(result);
		^result;
	}

	numChannels{
		^buffer.numChannels;
	}
}

