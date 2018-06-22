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

	test_SettingInvalidDataShouldReturnFalseAndNotSetData{

		var wasValid;
		var obj;
		var testData = [(0.0, 0.1..1.0), [0.1, 0.2]];
		obj = AuditFeatureData(\hello);
		wasValid = obj.setData(testData);
		this.assert(wasValid.not and: {obj.data.isNil}, "wasValid: % data: %".format(
			wasValid, obj.data
		));
	}

	test_loadMirFileData{
		var filepath = PathName(this.class.filenameSymbol.asString).pathOnly;
		var mirFile;
		var obj;
		filepath = filepath +/+  "data/mirTestData.scmirZ";
		mirFile = SCMIRAudioFile.newFromZ(filepath);
		obj = AuditFeatureData(\Loudness, mirFile: mirFile);

		this.assertEquals(
			obj.data.flat.size,
			mirFile.featuredata.size
		);
	}
}
