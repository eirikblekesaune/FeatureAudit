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
		headerFormat = "aiff",
		sampleFormat = "int32",
		target,
		filepath,
		addAction = \addToTail,
		analysisArgs
		|
		recRoutine = fork{
			var cond = Condition.new;
			var filename;
			var folder;
			var recSoundfile;
			var thisOne;
			folder = PathName(filepath).pathOnly;
			filename = PathName(filepath).fileNameWithoutExtension;

			tempRecFilepath	= "%/_temp_%.%".format(
				folder, filename, headerFormat
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

			recSynth = {
				var sig = In.ar(busnums);
				DiskOut.ar(recBuffer.bufnum, sig);
				Silent.ar(0);
			}.play(
				target: target.asTarget,
				addAction: addAction
			);

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
			recSynth.onFree({
				recIndicator.stop;
			});

			recDuration.wait;
			this.stopRecording(action);
		};
	}

	stopRecording{ arg action;
		forkIfNeeded{
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
			while({ soundFile.isOpen.not; }, {
				//"opening file".postln;
			});

			buffer = soundFile.asBuffer;
		};
	}

	normalize{
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
		soundFile = SoundFile.openRead(recFilepath);
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
