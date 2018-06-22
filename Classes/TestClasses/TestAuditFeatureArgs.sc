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
}
