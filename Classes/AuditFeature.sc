AuditFeature{
	var <name;
	var <settings;
	var <data;
	var <numItems;

	classvar <parameterSpecs;
	classvar itemSpecs;

	*initClass{
		this.initSpecs();
	}

	*new{arg name, settings, mirFile, startIndex = 0, numItems = 1, parameterSpecs, itemSpecs;
		^super.new.init(name, settings, mirFile, startIndex, numItems, parameterSpecs, itemSpecs);
   	}

	init{arg name_, settings_, mirFile, startIndex, numItems_, parameterSpecs_, itemSpecs_;
		name = name_;
		settings = settings_;
		numItems = numItems_;
		parameterSpecs = parameterSpecs_ ? this.class.parameterSpecs[name] ? [];
		itemSpecs = itemSpecs_ ? this.class.getItemSpecs(name, settings) ? [];
		if(mirFile.notNil, {
			this.importDataFromMirFile(mirFile, startIndex, numItems_);
		});
	}

	importDataFromMirFile{arg mirFile, startIndex = 0, numItems = 1;
		if(numItems == 1, {
			data = mirFile.featuredata.copySeries(
				startIndex, mirFile.numfeatures + startIndex
			);
			data = [data];
		}, {
			data = numItems.collect{arg i;
				var index = startIndex + i;
				mirFile.featuredata.copySeries(
					index,
					mirFile.numfeatures + index
				);
			};
		});
	}

	setData{arg arr;
		//a multichannel array of values between 0.0 and 1.0
		data = arr;
	}

	numSegments {
		^data[0].size;
	}

	segments{arg atItem = 0;
		^data[atItem];
	}

	makeView{arg parent, bounds, settings;
	}

	=={arg what;
		if(what.isKindOf(this.class).not, {
			^false;
		});
		^this.hash == what.hash;
	}

	hash{
		^this.instVarHash([\name, \settings]);
	}

	featureArgs{ ^[name] ++ settings; }

	*initSpecs{
		//specs are based on a combination is of the default sform SCMIR documentation
		//and the actual SCMIR Audiot file code, with that latter taking presedence.
		parameterSpecs = VTMOrderedIdentityDictionary[
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
		];

		//the return values from the feature analysis are based on SCMIR documetnation and SCMIRAudioFile
		//implementation
		itemSpecs = VTMOrderedIdentityDictionary[
			\MFCC -> {arg settings;
				var result;
				var numItems;
				if(settings.isNil, {
					numItems = parameterSpecs[\MFCC][\numcoeff].default;
				}, {
					if(settings.isEmpty, {
						numItems = parameterSpecs[\MFCC][\numcoeff].default;
					}, {
						numItems = settings.first;
					});
				});
				result = VTMOrderedIdentityDictionary.new;
				numItems.do({arg i;
					result.put(
						"c%".format(i).asSymbol,
						ControlSpec(0.0, 1.0)
					);
				});
				result;
			},
			\Chromagram -> {arg settings;
				var result, numItems;
				if(settings.isNil, {
					numItems = parameterSpecs[\Chromagram][\n].default;
				}, {
					if(settings.isEmpty, {
						numItems = parameterSpecs[\Chromagram][\n].default;
					}, {
						numItems = settings.first;
					});
				});
				result = VTMOrderedIdentityDictionary.new;
				numItems.do({arg i;
					result.put(
						"c%".format(i).asSymbol,
						ControlSpec(0.0, 1.0)
					);
				});
				result;
			},
			\KeyTrack -> {
				var notes = [
					'C', 'C#', 'D', 'Eb', 'E', 'F', 'F#', 'G', 'Ab', 'A', 'Bb', 'B'
				];
				VTMOrderedIdentityDictionary[
					\keyname -> OptionsSpec(
						notes ++ notes.collect({arg it; "% minor".format(it).asSymbol;
					}))
				];
			},
			\KeyMode -> {
				VTMOrderedIdentityDictionary[
					\mode -> OptionsSpec([\major, \minor, \chromatic])
				];
			},
			\Tartini -> {arg featureSettings;
				var result = VTMOrderedIdentityDictionary[
					\pitch -> \midi.asSpec.units_(\midinote)
				];
				if(featureSettings.notNil, {
					if(featureSettings.notEmpty, {
						result.put(\hasFreq, ControlSpec(0.0, 1.0));
					});
				});
				result;
			},
			\Tempo -> { 
				VTMOrderedIdentityDictionary[
					\bpm -> ControlSpec(0.0, 320.0)
				]
			},
			\Loudness -> {
				VTMOrderedIdentityDictionary[
					\sones -> ControlSpec(0, 64, units: "sones")
				];
			},
			\ZCR -> {
				VTMOrderedIdentityDictionary[
					\crossings -> \freq.asSpec
				]
			},
			\OnsetStatistics -> {
				VTMOrderedIdentityDictionary[
					\density -> ControlSpec(0.0, 100.0),
					\mean -> ControlSpec(0.0, 100.0),
					\stddev -> ControlSpec(0.0, 100.0)
				]
			},
			\BeatStatistics -> {
				VTMOrderedIdentityDictionary[
					\entropy -> ControlSpec(0.0, 1.0),
					\ratio -> ControlSpec(0.0, 1.0),
					\diversity -> ControlSpec(0.0, 1.0),
					\metricity -> ControlSpec(0.0, 1.0)
				]
			}
		];
	}

	*getItemSpecs{arg name, featureSettings;
		var result;
		if(itemSpecs.includesKey(name), {
			result = itemSpecs[name].value(featureSettings);
		}, {
			//if the item specs are not defined we assume that it is a single item spec
			result = VTMOrderedIdentityDictionary[
				\value -> ControlSpec(0.0, 1.0)
			];
		});
		^result;
	}
}
