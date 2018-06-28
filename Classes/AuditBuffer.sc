AuditBuffer {
	var <server;
	var <buffer; //TEMP getter
	var <mirFile;//TEMP getter
	var <mirFilepath;
	var <features;//TEMP getter [AuditFeatureData]
	var <soundFile;//TEMP getter
	var samples;//sclang sample values from buffer
	var range; //the main range for frames to be selected from
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

	findQualifiedSegmentIndexes{arg criterions;
		var result;
		var tempIndexes;
		tempIndexes = criterions.collect({arg criterion, i;
			criterion.findQualifiedSegmentIndexes(this).asSet;
		});
		result = tempIndexes.first;
		if(tempIndexes.size > 1, {
			tempIndexes[1..].do({arg item;
				result = result.sect(item);
			});
		});
		^result.asArray.sort;
	}

	findQualifiedStartTimes{arg criterions;
		^mirFile.frameStartTimes.atAll(
			this.findQualifiedSegmentIndexes(criterions);
		);
	}

	findQualifiedSegments{arg criterions;
		var result;
		result = this.findQualifiedSegmentIndexes(criterions);
		result = result.segmentConsecutiveFrames;
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
				[\KeyClarity]
			];
			mirFile = SCMIRAudioFile(pathName.fullPath, analysisArgs);

			"Starting analysis of '%'".format(
				pathName.fullPath
			).postln;
			mirFile.extractFeatures();
			mirFile.extractOnsets();
			"\tAnalysis DONE".postln;

			mirFilepath = "%%.scmirZ".format(
				pathName.pathOnly,
				pathName.fileNameWithoutExtension
			);
			mirFile.save(mirFilepath);
			features = this.class.prMakeFeatures(mirFile, analysisArgs);
			"features done: %".format(features).postln;
			action.value(mirFile);
		}
	}

	*prMakeFeatures{arg mirFile, featureArgsList;
		var result;
		var idxInfo;
		"Making features".postln;
		idxInfo = mirFile.featureinfo.collect(_.first);
		idxInfo = idxInfo +++ mirFile.resolveFeatureNumbers;
		result = IdentityDictionary.new;
		idxInfo.do{arg item, i;
			var featureName, startIndex, numItems;
			var featureObj;
			#featureName, startIndex, numItems = item;

			try{
				featureObj = AuditFeatureData(
					featureName,
					featureArgsList[i][1..],
					mirFile,
					startIndex,
					numItems
				)
			} {|err|
				"Failed to make AuditFeature obj for '%'".format(
					featureName
				).warn;
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

