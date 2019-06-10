TestAuditSection : VTMUnitTest {

	test_LT{
		var aa, bb;
		aa = AuditSection(0, 3);
		bb = AuditSection(4, 10);
		this.assert( aa < bb );
	}

	test_GT{
		var aa, bb;
		aa = AuditSection(0, 3);
		bb = AuditSection(4, 10);
		this.assert( bb > aa );
	}

	test_EQ{
		var aa, bb;
		aa = AuditSection(4, 3);
		bb = AuditSection(4, 10);
		this.assert( aa == bb );
	}

	test_NE{
		var aa, bb;
		aa = AuditSection(0, 3);
		bb = AuditSection(4, 10);
		this.assert( aa != bb );
	}

	test_LE{
		var aa, bb;
		aa = AuditSection(0, 3);
		bb = AuditSection(4, 10);
		this.assert( aa <= bb, "% <= %".format(aa, bb) );
		aa = AuditSection(4, 3);
		bb = AuditSection(4, 10);
		this.assert( aa <= bb, "% <= %".format(aa, bb) );
	}

	test_GE{
		var aa, bb;
		aa = AuditSection(0, 3);
		bb = AuditSection(4, 10);
		this.assert( bb >= aa, "% >= %".format(aa, bb) );
		aa = AuditSection(4, 3);
		bb = AuditSection(4, 10);
		this.assert( bb >= aa, "% >= %".format(aa, bb) );
	}
	
}
