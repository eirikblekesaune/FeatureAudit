TestAuditFeatureArgs : VTMUnitTest {

	test_MustHaveTypeArg{
		var obj;
		var testString = "Should fail when no type arg given";
		try{
			obj = AuditFeatureArgs();
			this.failed(thisMethod, testString);
		} {
			this.passed(thisMethod, testString);
		}
	}

	test_ShouldFailOnUnknownTypeArg{
		var obj;
		var testString = "Should fail when unknown type arg given";
		try{
			obj = AuditFeatureArgs('AnUnknownType');
			this.failed(thisMethod, testString);
		} {
			this.passed(thisMethod, testString);
		}
	}

	test_ArrayConversionNoArgs{
		var obj;
		obj = AuditFeatureArgs(\Loudness);
		this.assertEquals(obj.asArray, [\Loudness]);
	}

	test_ArrayConversionWithOneArg{
		var obj;
		obj = AuditFeatureArgs(\Tartini, [1]);
		this.assertEquals(obj.asArray, [\Tartini, 1]);
	}

	test_ArrayConversionWithMultipleArgs{
		var obj;
		var args;
		AuditFeatureArgs.specs[\Loudness].keysValuesDo({arg key, val;
			args = args.add(rrand(val.minval, val.maxval));
		});
		obj = AuditFeatureArgs(\Loudness, args);
		this.assertEquals(obj.asArray, [\Loudness] ++ args);
	}

	test_SetGetArgsByKey{
		//test all that have args to the feature
		AuditFeatureArgs.specs.select({arg item;
			item.notEmpty;
		}).keysValuesDo({arg type, spec;
			var obj;
			var args, newArgs;
			obj = this.class.makeRandom(type: type);
			args = obj.args;
			obj.specs.keysValuesDo({arg specKey, spec;
				var newVal = spec.random;
				newArgs = newArgs.add(newVal);
				obj.set(specKey, newVal);
				this.assertEquals(
					obj.get(specKey), newVal,
					"Should set feature args for '%' key: '%'".format(
						type, specKey
					)
				);
			});
		});
	}

	*makeRandom{arg type;
		var result;
		var args;
		if(type.isNil, {
			type = AuditFeatureArgs.specs.keys.choose;
		});
		AuditFeatureArgs.specs[type].do{arg item;
			args = args.add(item.random);
		};
		result = AuditFeatureArgs(type, args);
		^result;
	}
}
