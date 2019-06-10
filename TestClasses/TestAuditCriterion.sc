TestAuditCriterion : UnitTest {

	test_CustomTestFunction{
		var obj;
		var featureObj;
		var segments;
		obj = AuditCriterion(\myCri);
		obj.testFunction_({arg val, i;
			var result;
			result = val > 22 and: {val < 66};//should return 2,3,4
			result;
		});
		featureObj = AuditFeatureData(\myFeat, data: [[11,22,33,44,55,66,77,88,99]]);
		segments = obj.findQualifiedSegmentIndexes(featureObj);
		this.assertEquals(segments, [2,3,4]);
	}

	test_getSectionObjects{
		var obj;
		var featureObj;
		var testData = [(0.0, 0.1..1.0)];
		var sections;
		
		obj = AuditRangeCriterion('silence', max: -20.dbamp);
		obj = obj or: AuditRangeCriterion('loudest', min: -6.dbamp);
		featureObj = AuditFeatureData(\myData, data: testData );
		sections = obj.findQualifiedSections(featureObj);
		this.assertEquals(
			sections, [AuditSection(0,1), AuditSection(6,10)]
		);
	}

}
