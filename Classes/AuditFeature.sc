AuditFeature{
	var <name;
	var <featureArgs;
	var <data;
	var <numItems;

	classvar itemSpecs;

	*initClass{
		this.initSpecs();
	}

	*new{arg name, featureArgs, mirFile, startIndex = 0, numItems = 1, itemSpecs;
		^super.new.init(name, featureArgs, mirFile, startIndex, numItems, itemSpecs);
   	}

	init{arg name_, featureArgs_, mirFile, startIndex, numItems_, itemSpecs_;
		name = name_;
		featureArgs = featureArgs_;
		numItems = numItems_;
		itemSpecs = itemSpecs_ ? this.class.getItemSpecs(name, featureArgs) ? [];
		if(mirFile.notNil, {
			this.importDataFromMirFile(mirFile, startIndex, numItems_);
		});
	}

	importDataFromMirFile{arg mirFile, startIndex = 0, numItems = 1;
		var result;
		if(numItems == 1, {
			result = mirFile.featuredata.copySeries(
				startIndex, mirFile.numfeatures + startIndex
			);
			result = [result];
		}, {
			result = numItems.collect{arg i;
				var index = startIndex + i;
				mirFile.featuredata.copySeries(
					index,
					mirFile.numfeatures + index
				);
			};
		});
		^this.setData(result);
	}

	//returns boolean true if data was valid
	setData{arg arr;
		//a multichannel array of values between 0.0 and 1.0
		if(this.class.validateData(arr), {
			data = arr.deepCopy;
			^true;
		}, {
			^false;
		});
	}

	*validateData{arg dt;
		//all data items must have same size
		^dt.collect(_.size).asSet.size == 1;
	}

	numSegments {
		^data[0].size;
	}

	segments{arg atItem = 0;
		^data[atItem];
	}

	makeView{arg parent, bounds, featureArgs;
	}

	=={arg what;
		if(what.isKindOf(this.class).not, {
			^false;
		});
		^this.hash == what.hash;
	}

	hash{
		^this.instVarHash([\data, \featureArgs]);
	}

	*initSpecs{
		//the return values from the feature analysis are based on SCMIR documetnation and SCMIRAudioFile
		//implementation
		itemSpecs = VTMOrderedIdentityDictionary[
			\MFCC -> {arg featureArgs;
				var result;
				var numItems;
				if(featureArgs.isNil, {
					numItems = featureArgs.at(\numcoeff);
				}, {
					if(featureArgs.isEmpty, {
						numItems = AuditFeatureArgs.specs[\MFCC][\numcoeff].default;
					}, {
						numItems = featureArgs.at(\numcoeff);
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
			\Chromagram -> {arg featureArgs;
				var result, numItems;
				if(featureArgs.isNil, {
					numItems = featureArgs.at(\n);
				}, {
					if(featureArgs.isEmpty, {
						numItems = AuditFeatureArgs.specs[\Chromagram][\n].default;
					}, {
						numItems = featureArgs.at(\n);
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
