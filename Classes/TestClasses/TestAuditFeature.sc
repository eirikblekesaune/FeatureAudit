TestAuditFeatureData : VTMUnitTest {

	test_loadDataManually{
		var obj;
		var testData = [(0.0, 0.1..1.0), (1.0, 0.9..0.0)];
		obj = AuditFeatureData(\myFeature, data: testData);
		this.assertEquals(obj.data, testData);
	}

	test_dataIntegrity{
		var obj;
		var testData = [(0.0, 0.1..1.0), (1.0, 0.9..0.0)];
		obj = AuditFeatureData(\hello, data: testData);
		this.assert(obj.data !== testData, "data must be a copy");
	}

	test_SettingInvalidDataShouldThrowError{
		var numErrors = 0;
		var obj;
		var testDataArray = [
			[(0.0, 0.1..1.0), [0.1, 0.2]],//unequal size
			[], //empty array
			nil, // nil
			12, //non array type object
		];
		testDataArray.do{arg testData;
			try{
				obj = AuditFeatureData(\hello, data: testData);
			} {|err|
				//Shoudl fail
				numErrors = numErrors + 1;
			};
		};
		this.assertEquals(numErrors, testDataArray.size);
	}

	test_loadMirFileData{
		var filepath = PathName(this.class.filenameSymbol.asString).pathOnly;
		var mirFile;
		var obj;
		filepath = filepath +/+  "data/mirTestData.scmirZ";
		mirFile = SCMIRAudioFile.newFromZ(filepath);
		obj = AuditFeatureData.newFromMirFile(\myLoudness,
			mirFile: mirFile);

		this.assertEquals(
			obj.data.flat.size,
			mirFile.featuredata.size
		);
	}
}
