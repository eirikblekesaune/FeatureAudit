AuditRecordBuffer : AuditBuffer {
	var recBuffer;
	var recSynth;
	var recFilepath;
	var tempRecFilepath;
	var numChannels;
	var recIndicator;
	var recRoutine;

	*new{arg server;
		^super.new(server).initAuditRecordBuffer;
	}

	initAuditRecordBuffer{
	}

	free{arg action;
		this.abortRecording;
		super.free(action);
	}

	startRecording{
		|
		busnums,
		recDuration = 30,
		action,
		recFolder,
		name = "rAuditRec",
		headerFormat = "aiff",
		sampleFormat = "int32"
		|
		recRoutine = fork{
			var cond = Condition.new;
			var stamp = Date.getDate.stamp;
			var recSoundfile;
			var thisOne;

			recFilepath = "%%%.%".format(
				recFolder, name, stamp, headerFormat
			);
			tempRecFilepath	= "%_temp_%%.%".format(
				recFolder, name, stamp, headerFormat
			);
			recBuffer = Buffer.alloc(server, server.sampleRate.nextPowerOfTwo, busnums.size,
				{ cond.test = true; cond.signal; }
			);
			cond.wait;
			cond.test = false;
			recBuffer.write(
				tempRecFilepath,
				headerFormat: headerFormat,
				sampleFormat: sampleFormat,
				numFrames:0,
				startFrame:0,
				leaveOpen: true,
				completionMessage: { cond.test = true; cond.signal; }
			);
			cond.wait;
			cond.test = false;

			recSynth = NodeProxy.audio(server, busnums.size);
			recSynth.source = {
				var sig = SoundIn.ar(busnums);
				DiskOut.ar(recBuffer.bufnum, sig);
			};
			"Started recording '%'".format(tempRecFilepath).postln;
			thisOne = thisThread;
			recIndicator = fork{
				var i = 0;
				loop{
					if(thisOne.isPlaying.not, {
						thisThread.stop;
					});
					1.0.wait;
					i = i + 1;
					"\tRecording dur: % of % from busnums: % [%]".format(
						i, recDuration, busnums, recFilepath
					).postln;
				}
			};
			recSynth.addDependant({arg whoChanged, whatChanged;
				if(whatChanged == \free, {
					// "IT WAS FREED".postln;
					recIndicator.stop;
				});
			});

			recDuration.wait;
			this.stopRecording(action);
		};
	}

	stopRecording{arg action, doAnalysis = true, doNormalize = true, doLoadBuffer = true;
		fork{
			var cond = Condition.new;

			if(recIndicator.notNil and: {recIndicator.isPlaying}, {
				recIndicator.stop;
			});
			"Stopping recording '%'".format(tempRecFilepath).postln;
			recSynth.free;
			0.1.wait;
			if(recBuffer.notNil, {
				try{ recBuffer.close; }
			});

			0.2.wait;
			if(recBuffer.notNil, {
				try{ recBuffer.free({ cond.test = true; cond.signal; }); };
				recBuffer = nil;
			}, {
				cond.test = true; cond.signal;
			});
			cond.wait;
			cond.test = false;
			if(doNormalize, {
				var tempSoundFile;
				tempSoundFile = SoundFile.openRead(tempRecFilepath);
				while({ tempSoundFile.isOpen.not; }, {
					//"opening file".postln;
				});
				// "Normalizing recording '%'".format(recFilepath).postln;
				tempSoundFile.normalize( recFilepath );
				tempSoundFile.close;
				while({ tempSoundFile.isOpen }, {
					// "Closing file".postln;
				});
				File.delete(tempRecFilepath);


			});
			soundFile = SoundFile.openRead(recFilepath);
			while({ soundFile.isOpen.not; }, {
				//"opening file".postln;
			});

			if(doLoadBuffer, {
				buffer = soundFile.asBuffer;
			});

			if(doAnalysis, {
				this.analyze(
					action: action
				);
			});
		};
	}

	abortRecording{
		fork{
			recRoutine.stop;
			recSynth.free;
			if(recBuffer.notNil, {
				recBuffer.close;
				recBuffer.free;
			});
		}
	}

}
