AuditFeatureData{
	var <featureName;
	var <data;
	var <specs;
	var <featureArgsDict;
	var <>auditBuf;

	classvar <specs;

	*initClass{
		this.initSpecs();
	}

	*new{arg featureName, featureArgs, data, specs;
		if(this.isValidData(data).not, {
			Error("invalid feature data").throw;
			^nil;
		});
		^super.new().init(featureName, featureArgs.deepCopy, data.deepCopy, specs);
	}

	*newFromMirFile{arg featureName, featureArgs, mirFile, startIndex, numItems,  specs;
		var data;
		data = this.getDataFromMirFile(mirFile, startIndex, numItems);
		^this.new(featureName, featureArgs, data, specs);
	}

	init{arg featureName_, featureArgs_, data_, specs_;
		var featureArgsSpecs;
		featureName = featureName_;
		//the data inits to a dict
		data = VTMOrderedIdentityDictionary.new;

		featureArgsSpecs = AuditAnalysisArgs.getSpecs(featureName);
		featureArgsDict = VTMOrderedIdentityDictionary.new;
		if(featureArgs_.notNil, {
			featureArgsSpecs.keysValuesDo({arg featureArgName, featureArgSpec, i;
				featureArgsDict.put(featureArgName, featureArgs_[i]);
			});
		});

		specs = specs_ ? this.class.getSpecs(featureName, featureArgsDict);

		if(specs.notNil, {
			//We found specs for this feature name.
			//The order of iteration corrsponds to the order in SCMIR lib
			specs.keysValuesDo({arg analysisArgName, analysisSpec, i;
				data.put(analysisArgName, data_.at(i));
			});
		}, {
			//Did not specs for this feature name.
			//We now want a single item data object, using the itemName 'val'
			if(data_.size == 1, {
				specs = VTMOrderedIdentityDictionary[\val -> ControlSpec.new];
				data.put( \val, data_.at(0) );
			}, {
				var tempData, tempSize;
				//make each segment multi item data
				tempSize = data_.size;
				tempData = data_.flop.flat.clump(tempSize);
				data.put(\val, tempData);
			})
		});
	}

	*getDataFromMirFile{arg mirFile, startIndex = 0, numItems = 1;
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
		^result;
	}

	numItems{
		^data.size;
	}

	itemNames{ ^data.keys; }

	featureArgs { ^featureArgsDict.values; }

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

	*isValidData{arg dt;
		//all data items must be array with the same size
		if(dt.isNil, {^false});
		if(dt.isArray.not, {^false});
		if(dt.isEmpty, {^false;});
		^dt.collect(_.size).asSet.size == 1;
	}

	numSegments {
		^data.first.size;
	}

	segments{arg atItem = \val;
		^data[atItem];
	}

	findQualifiedSegmentIndexes{arg criterion;
		var result = [];
		// criterion
		this.segments.do({arg val, i;
			if(criterion.qualify(val), {
				result = result.add(i);
			});
		});
		^result;
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
		^this.instVarHash([\data, \featureArgsDict]);
	}

	*initSpecs{
		//the return values from the feature analysis are based on SCMIR documentation and SCMIRAudioFile
		//implementation
		specs = VTMOrderedIdentityDictionary[
			\MFCC -> {arg featureArgsDict;
				var result;
				var numItems;
				if(featureArgsDict.isNil, {
					numItems = featureArgsDict.at(\numcoeff);
				}, {
					if(featureArgsDict.isEmpty, {
						numItems = AuditAnalysisArgs.specs[\MFCC][\numcoeff].default;
					}, {
						numItems = featureArgsDict.at(\numcoeff);
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
			\Chromagram -> {arg featureArgsDict;
				var result, numItems;
				if(featureArgsDict.isNil, {
					numItems = featureArgsDict.at(\n);
				}, {
					if(featureArgsDict.isEmpty, {
						numItems = AuditFeatureArgs.specs[\Chromagram][\n].default;
					}, {
						numItems = featureArgsDict.at(\n)
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
			\Tartini -> {arg featureArgsDict;
				var result = VTMOrderedIdentityDictionary[
					\pitch -> \midi.asSpec.units_(\midinote)
				];
				if(featureArgsDict.notEmpty, {
					result.put(\hasFreq, ControlSpec(0.0, 1.0));
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

	*getSpecs{arg featureName, featureArgsDict;
		var result;
		if(specs.includesKey(featureName), {
			result = specs[featureName].value(featureArgsDict);
		});
		^result;
	}
}
