AuditBuffer {
	var <server;
	var <buffer; //TEMP getter
	var <mirFile;//TEMP getter
	var <mirFilepath;
	var <features;//TEMP getter [AuditFeatureData]
	var <soundFile;//TEMP getter
	var samples;//sclang sample values from buffer
	var frames;
	var channelPeaks; //dictionary with index: peakValue
	var normalizeFactor;
	var <markers;

	*new{arg server;
		^super.new.initAuditBuffer(server);
	}

	initAuditBuffer{arg server_;
		server = server_ ? Server.default;
		markers = [];
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
				Error("Did not find featuredata for criterion arg: '%'".format(criterion)
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
		^soundFile.numChannels;
	}

	plot{
		//TODO: plot buffer and features here
	}

	plotFeature{arg which, itemKey;
		if(features.includesKey(which), {
			var data = features[which].data;
			if(itemKey.isNil, {
				data.values.asArray.plot;
			}, {
				data[itemKey].plot;
			});
		});

	}

	//optionally with arguments
	//async function
	channelPeaks{arg startFrame = 0, numFrames, chunkSize = 1048576, threaded = false;
		var result;
		var rawData, peak, numChunks, chunksDone, test;
		var shouldCache = false;
		//if we already have found the peaks and the method call
		//wants the whole file we just return the cached value.
		if(numFrames.isNil, {
			if(channelPeaks.notNil, {
				^channelPeaks;
			}, {
				//cache only this concernes the whole sound file.
				shouldCache = true;
			});
		});

		//this code is based on the the code from SoundFile.channelPeaks
		peak = 0 ! this.numChannels;
		numFrames = numFrames ? this.numFrames;
		numFrames = numFrames * this.numChannels;

		if(threaded) {
			numChunks = (numFrames / chunkSize).roundUp(1);
			chunksDone = 0;
		};

		soundFile.seek(startFrame, 0);
		while(
			{ (numFrames > 0) and: {
				rawData = FloatArray.newClear(min(numFrames, chunkSize));
				soundFile.readData(rawData);
				rawData.size > 0;
			}},
			{
				rawData.do({ |samp, i|
					if(samp.abs > peak[i % this.numChannels], {
						peak[i % this.numChannels] = samp.abs;
					});
				});
				numFrames = numFrames - chunkSize;
				if(threaded, {
					chunksDone = chunksDone + 1;
					test = chunksDone / numChunks;
					if(	((chunksDone-1) / numChunks) < test.round(0.02)
						and: { test >= test.round(0.02) },
						{
							$..post;
						}
					);
					0.01.wait;
				});
			}
		);
		if(threaded) { $\n.postln };

		//Cache the channel peaks for the whole file if this was the
		//result.
		if(shouldCache, {
			channelPeaks = peak;
		});
		^peak
	}

	normalizeFactor{arg findPeaksIfNil = false, chunkSize = 1048576, threaded = false;
		if(channelPeaks.notNil, {
			^normalizeFactor = channelPeaks.maxItem.reciprocal;
		}, {
			if(findPeaksIfNil, {
				this.channelPeaks(chunkSize, threaded);
				^this.normalizeFactor;
			}, {
				"AuditBuffer:normalizeFactor - channelPeaks not calculated yet".warn;
				^1.0;
			});
		});
	}

	sampleRate{
		^soundFile.sampleRate;
	}

	numFrames{
		^soundFile.numFrames;
	}

	importMarkersRegionsFromReaper{arg csvFilepath;
		var result = [], fileInput;
		if(File.exists(csvFilepath), {
			fileInput = CSVFileReader.readDictionaries(
				csvFilepath
			);
			fileInput.collect({arg it;
				result = result.add(
					AuditRegion.newFromReaperMarker(it, this)
				);
			});
			markers = markers.addAll(result);
			^result;
		}, {
			"Reaper marker file not found: '%'".format(csvFilepath).warn;
			^nil;
		});
	}

	localMirFilePath{
		var pn = PathName(soundFile.path);
		var result = pn.pathOnly ++ pn.fileNameWithoutExtension ++ ".scmirZ";
		^result;
	}

	localMirFileExists{
		^File.exists(this.localMirFilePath);
	}

	*synthDefs{
		^[1,2,4].collect({arg numChannels;
			SynthDef("audBufPlayFixed%".format(numChannels).asSymbol, {
				|
				bufnum, start = 0, attack = 0.0, decay = 0.0, sus = 1.0, release = 0.0,
				amp = 0.1, out = 0, dur = 1.0, rate = 1.0, loop = 0
				|
				var sig, env;
				var sustainTime = dur - attack - decay - release;
				var endFrameTrigger = TDuty.ar(dur);
				env = EnvGen.kr(
					Env([0.00001, 1.0, sus, sus, 0.00001],
						[attack, decay, sustainTime, release],
						\exp),
					doneAction: 2
				).poll;
				sig = PlayBuf.ar(
					numChannels,
					bufnum,
					rate,
					trigger: endFrameTrigger * loop,
					startPos: start,
					loop: loop
				);
				Out.ar(0, sig * env * amp);
			});
		});
	}

}

