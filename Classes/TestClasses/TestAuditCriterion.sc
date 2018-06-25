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

}
