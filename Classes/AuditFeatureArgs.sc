AuditFeatureArgs {
	var name;
	var args;
	classvar <specs;

	*new{arg name, args;
		^super.newCopyArgs(name).init(args);
	}

	init{arg args_;
		
	}

	asSCMIRArgs{ ^[name] ++ args.values; }

	=={arg what;
		if(what.isKindOf(this.class).not, {
			^false;
		});
		^this.hash == what.hash;
	}

	hash{
		^this.instVarHash([\name, \args]);
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
			]
		]
	}
}
