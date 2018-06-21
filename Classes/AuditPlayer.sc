/*
TODOs:
- Make derivative criterions, i.e. selection on rate of change in feature data
- Gather list of 'operations' that can do offline rendering etc.
- make all the segments AND blocks available for individual playback
  This will be good for auditing and composing.
  These units can be used to compose with by indexing.
- make drag and drop into phrases, in order to make phrases and/or cycles from segments.
- offer onset segmentation, another playback method.
- static segment duration option
   - also possible to make harmonic durations for the static duration
- sustain duration, overlapping segments playback
- tape playback start and stop pitch shift. i.e. pitch rises upon start and falls upon stop.
- Store segment playback log for each playback.
- Possibility to zip two or more segment streams. Maybe from two different players? Or different sound files?
- find normalization factor from maximun in all selected segments.
- Use criterions also for sending control signals, callbacks, or setting control buses.
  this can be used to control send effects, filters, or other processes.
- Criterions are different from features.
- Offer sorting capability so you can play pitches with the sound file segments.
  e.g. play segment sprted by duration or pitch etc.
*/

AuditPlayer{
	var <server;
	var <criterions;//TEMP getter
	var <buffers;
	var preSkirt, postSkirt;//how many millis before and after the criterions are to be included
	var process;
	var sustain;

	classvar <synthDefs;

	var synthDefsLoaded = false;

	synthDefName{
		^\featureplayer;
	}

	*new{arg server;
		^super.new.init(server);
	}

	init{arg serv;
		server = serv ? Server.default;
	}

	//load synth defs for playback selected segments to server
	*loadSynthDefs{arg server;
		"Loading FeatureAuditPlayer SynthDefs".postln;
		[1,2].do{arg numChannels;
			SynthDef("%%".format(this.synthDefName, numChannels).asSymbol, {
				|
				bufnum, amp = 0.1, startSeconds = 0,
				duration = 1, pan = 0.0, i_out = 0,
				attack = 0.005, release = 0.005, normFactor = 1.0
				|
				var sig, env, progress;
				env = EnvGen.kr(
					Env(
						[0.0001, 1.0, 1.0, 0.0001],
						[attack, duration - attack - release, release],
						\exp
					),
					//						BinaryOpUGen(
					//							'<=',
					//							duration - 0.01,
					//							Sweep.kr()
					//						).not,
					doneAction: Done.freeSelf
				);
				//Sweep.kr().poll(label: \sweep);//to verify duration
				sig = PlayBuf.ar(
					numChannels: numChannels,
					bufnum: bufnum,
					rate: BufRateScale.kr(bufnum),
					startPos: BufSampleRate.kr(bufnum) * startSeconds
				);
				sig = sig * env * amp;
				switch(numChannels,
					1, {
						sig = Pan2.ar(sig, pan);
					},
					2, {
						sig = Balance2.ar(sig[0], sig[1], pan);
					},
					{
						sig = Splay.ar(sig, center: pan);
					}
				);
				OffsetOut.ar(i_out, sig * normFactor);
			}).add;
		};
	}

	//get the synth def name based on buffer arg, get num channels from buffer
	getSynthDefName{
		^"%%".format(this.synthDefName, this.numChannels).asSymbol;
	}


	free{}

	play{arg action;
		if(server.serverRunning.not, {
			"AuditPlayer:play - Server not running".warn;
			^this;
		});
		forkIfNeeded{
			if(synthDefsLoaded.not, {
				this.class.loadSynthDefs(server);
			});
			server.sync;
			process = this.makeProcess;
			process.play;
			"Started playing AuditPlayer".postln;
			action.value(this);
		}
	}

	makeProcess{
	}

	isPlaying{ ^(process.notNil and: {process.isPlaying;}) }

	stop{
		if(this.isPlaying, {
			process.stop;
		});
	}
}

