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


	*makeRandom{
		var result;
		var type, args;
		type = AuditFeatureArgs.specs.keys.choose;
		AuditFeatureArgs.specs[type].do{arg item;
			args = args.add(item.random);
		};
		result = AuditFeatureArgs(type, args);
		^result;
	}
}
