TestAuditAnalysisArgs : VTMUnitTest {

	test_MustHaveTypeArg{
		var obj;
		var testString = "Should fail when no type arg given";
		try{
			obj = AuditAnalysisArgs();
			this.failed(thisMethod, testString);
		} {
			this.passed(thisMethod, testString);
		}
	}

	test_ShouldFailOnUnknownTypeArg{
		var obj;
		var testString = "Should fail when unknown type arg given";
		try{
			obj = AuditAnalysisArgs('AnUnknownType');
			this.failed(thisMethod, testString);
		} {
			this.passed(thisMethod, testString);
		}
	}

	test_ArrayConversionNoArgs{
		var obj;
		obj = AuditAnalysisArgs(\RMS);
		this.assertEquals(obj.asArray, [\RMS]);
	}

	test_ArrayConversionWithOneArg{
		var obj;
		obj = AuditAnalysisArgs(\Tartini, [1]);
		this.assertEquals(obj.asArray, [\Tartini, 1]);
	}

	test_ArrayConversionWithMultipleArgs{
		var obj;
		var args;
		AuditAnalysisArgs.specs[\Loudness].keysValuesDo({arg key, val;
			args = args.add(rrand(val.minval, val.maxval));
		});
		obj = AuditAnalysisArgs(\Loudness, args);
		this.assertEquals(obj.asArray, [\Loudness] ++ args);
	}

	test_SetGetArgsByKey{
		//test all that have args to the feature
		AuditAnalysisArgs.specs.select({arg item;
			item.notEmpty;
		}).keysValuesDo({arg featureName, spec;
			var obj;
			var args, newArgs;
			obj = this.class.makeRandom(featureName: featureName);
			args = obj.args;
			obj.specs.keysValuesDo({arg specKey, spec;
				var newVal = spec.random;
				newArgs = newArgs.add(newVal);
				obj.set(specKey, newVal);
				this.assertEquals(
					obj.get(specKey), newVal,
					"Should set feature args for '%' key: '%'".format(
						featureName, specKey
					)
				);
			});
		});
	}

	*makeRandom{arg featureName;
		var result;
		var args;
		if(featureName.isNil, {
			featureName = AuditAnalysisArgs.specs.keys.choose;
		});
		AuditAnalysisArgs.specs[featureName].do{arg item;
			args = args.add(item.random);
		};
		result = AuditAnalysisArgs(featureName, args);
		^result;
	}
}
