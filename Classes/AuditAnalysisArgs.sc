AuditAnalysisArgs {
	var <featureName;//the featureName of analysis data e.g. Loudness
	var <args;
	var <specs;

	classvar <specs;

	*new{arg featureName, args;
		if(featureName.isNil or: {specs.includesKey(featureName).not}, {
			Error("Unrecognized feature name: '%'".format(featureName)).throw;
		});
		^super.new.init(featureName, args);
	}

	init{arg featureName_, args_;
		featureName = featureName_;
		specs = this.class.getSpecs(featureName);
		if(args_.isNil, {
			args = specs.collect(_.default);
		}, {
			args = args_;
		});
	}

	set{arg key, val;
		var index;
		index = this.getArgsIndex(key);
		if(index.notNil, {
			args.put(index, val);
		});
	}

	get{arg key;
		var result;
		var index = this.getArgsIndex(key);
		if(index.notNil, {
			result = args[index];
		});
		^result;
	}
	
	getArgsIndex{|key|
		var index;
		if(specs.includesKey(key), {
			index = specs.keys.indexOf(key);
		});
		^index;
	}

	asSCMIRArgs{
		var result = [featureName];
		if(args.notNil or: {args.isEmpty}, {
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

	getSpec{|argKey|
		^specs[argKey];
	}

	hash{
		^this.instVarHash([\featureName, \args]);
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

	*getSpecs{arg featureName;
		var result = VTMOrderedIdentityDictionary.new;
		if(this.specs.includesKey(featureName), {
			result = this.specs[featureName];
		});
		^result;
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
