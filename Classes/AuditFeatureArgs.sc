AuditFeatureArgs {
	var <type;//the type of analysis data e.g. Loudness
	var <args;
	var <specs;

	classvar <specs;

	*new{arg type, args, specs;
		if(type.isNil, {
			Error("must have feature args type").throw;
			^nil;
		});
		if(this.specs.includesKey(type).not, {
			Error("unknown feature args type: ''".format(type)).throw;
			^nil;
		});
		^super.newCopyArgs(type, args).init(specs);
	}

	init{arg specs_;
		specs = specs_ ?? {
			this.class.specs[type];
		};
	}

	set{arg key, val;
		var index = this.class.specs[type].keys.indexOf(key);
		args.put(index, val);
	}

	get{arg key;
		var result;
		var index = this.class.specs[type].keys.indexOf(key);
		result = args[index];
		^result;
	}

	asSCMIRArgs{
		var result = [type];
		if(args.notNil, {
			result = result ++ args;
		});
		^result;
	}
	asArray{ ^this.asSCMIRArgs; }

	=={arg what;
		if(what.isKindOf(this.class).not, {
			^false;
		});
		^this.hash == what.hash;
	}

	numArgs{ ^args.size; }
	keys{ ^specs.keys; }

	hash{
		^this.instVarHash([\type, \args]);
	}

	printOn{arg stream;
		stream << this.class.name;
		stream << this.asArray;
	}

	makeView{arg parent, bounds, featureArgs;
		var viewUpdater;
		var specControls;
		var specControlsView;
		specControls = this.specs.collect({arg spec, specKey;
			"spec: % key: %".format(spec, specKey).postln;
		});
//		specControlsView = View().layout_(VLayout(
//		);
//		^View(parent, bounds)
	}

	*initClass{
		Class.initClassTree(VTMOrderedIdentityDictionary);
		specs = VTMOrderedIdentityDictionary[
			\MFCC -> VTMOrderedIdentityDictionary[
				\numcoeff -> ControlSpec(2, 42, step: 1, default: 13)
			],
			\Chromagram -> VTMOrderedIdentityDictionary[
				\n -> ControlSpec(2, 53, default: 12, step: 1, units: 'divisions'),
				\tuningbase -> ControlSpec(1.0, 120.0, default: 32.703195662575, units: 'Hz'),
				\octaves -> ControlSpec(1, 16, default: 8, step: 1, units: 'octaves'),
				\integrationflag -> ControlSpec(0, 1, default: 0, step: 1),
				\coeff -> ControlSpec(0.001, 1.0, default: 0.9),
				\octaveratio -> ControlSpec(1, 10, default: 2, step: 1),
				\perframenormalize -> ControlSpec(0, 1.0, default: 0)
			],
			\KeyClarity -> VTMOrderedIdentityDictionary[
				\keydecay -> ControlSpec(0.01, 10.0, default: 2.0, units: 'secs'),
				\chromaleak -> ControlSpec(0.0, 1.0, default: 0.5)
			],
			\KeyTrack -> VTMOrderedIdentityDictionary[
				\keydecay -> ControlSpec(0.01, 10.0, default: 2.0, units: 'secs'),
				\chromaleak -> ControlSpec(0.0, 1.0, default: 0.5)
			],
			\KeyMode -> VTMOrderedIdentityDictionary[
				\keydecay -> ControlSpec(0.01, 10.0, default: 2.0, units: 'secs'),
				\chromaleak -> ControlSpec(0.0, 1.0, default: 0.5)
			],
			\SpectralEntropy -> [], // no args
			//With SCMIR these are hard coded, so setting them won't have any effect
			//\Tartini -> VTMOrderedIdentityDictionary[
			//	\threshold -> ControlSpec(0.0, 1.0, default: 0.93, units: 'amp'),
			//	\n -> ControlSpec(2, 2048, default: 2048),
			//	\k -> ControlSpec(0, 2048, default: 0),
			//	\overlap -> ControlSpec(2, 1024, default: 1024),
			//	\smallCutoff -> ControlSpec(0.1, 1.0, default: 0.5)
			//],
			\Tartini -> [],
			\Tempo -> [], //no args in SCMIR
			\Loudness -> VTMOrderedIdentityDictionary[
				\smask -> ControlSpec(0.0, 1.0, default: 0.25),
				\tmask -> ControlSpec(0.0, 1.0, default: 1.0)
			],
			\SensoryDissonance -> VTMOrderedIdentityDictionary[
				\maxpeaks -> ControlSpec(1, 250, default: 100, step: 1),
				\peakthreshold -> ControlSpec(0.0, 1.0, default: 0.1),
				\norm -> ControlSpec(0.0, 10.0, default: 1.0),
				\clamp -> ControlSpec(0.0, 1.0, default: 1.0)
			],
			\SpecCentroid -> [],// no args
			\SpecPcile -> VTMOrderedIdentityDictionary[
				\fraction -> ControlSpec(0.0, 1.0, default: 0.5),
				\interpolate -> ControlSpec(0, 1, default: 0, step: 1)
			],
			\SpecFlatness -> [], // no args
			\FFTCrest -> VTMOrderedIdentityDictionary[
				\freqlo -> ControlSpec(0.0, 50000.0, default: 0.0),
				\freqhi -> ControlSpec(0.0, 50000.0, default: 50000.0)
			],
			\FFTSpread -> [],//no args
			\FFTSlope -> [], // no args
			\Onsets -> VTMOrderedIdentityDictionary[
				\odftype -> OptionsSpec(
					[\power, \magsum, \complex, \rcomplex, \phase, \wphase, \mkl],
					default: \rcomplex
				)
			],
			\RMS -> [], //no args
			\ZCR -> [], //no args
			\AttackSlope -> [], // no args
			\Transient -> VTMOrderedIdentityDictionary[
				\branchthreshold -> ControlSpec(0.0, 1.0, default: 0.5),
				\prunethreshold -> ControlSpec(0.0, 1.0, default: 0.1)
			],
			\OnsetStatistics -> VTMOrderedIdentityDictionary[
				\windowsize -> ControlSpec(0.1, 5.0, default: 2.0, units: \seconds),
				\threshold -> ControlSpec(0.0, 1.0, default: 0.125, units: 'amp'),
			],
			\BeatStatistics -> VTMOrderedIdentityDictionary[
				\leak -> ControlSpec(0.01, 0.999, default: 0.95),
				\numpreviousbeats -> ControlSpec(0, 32, default: 4)
			],
			\CustomFeature -> []//VTMOrderedIdentityDictionary[
				//function that checks if incoming value is a function that defines
				//a closed function.
				//\function -> {|val| val.isFunction and: {val.isClosed;};},
				//\numItems -> ControlSpec(0, 100, default: 1)
			//];
		]
	}
}
