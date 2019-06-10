TestAuditBuffer : UnitTest {

	test_makeNewDefault{
		var obj = AuditBuffer(Server.default);

		this.assert(
			obj.notNil && {obj.class == AuditBuffer},
			"AuditBuffer made object"
		);
		this.assert(
			obj.server === Server.default,
			"AuditBuffer set server field"
		);
	}
}
