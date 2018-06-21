AuditCriterion{
	var <name;
	var <>specs;
	var <>active = true;
	var <inverse = false;
	var >testFunction;


	*loudness{arg min, max;
		^AuditRangeCriterion(
			\Loudness,
			VTMOrderedIdentityDictionary[
				\min -> \db.asSpec,
				\max -> \db.asSpec.default_(0.0)
			],
			min, max
		);
	}

	*pitch{arg min, max;
		^AuditRangeCriterion(
			\Tartini,
			VTMOrderedIdentityDictionary[
				\min -> \freq.asSpec.default_(20),
				\max -> \freq.asSpec.default_(20000)
			],
			min, max
		);
	}

	*new{arg name, specs;
		^super.newCopyArgs( name, specs );
	}

	findQualifiedSegmentIndexes{arg auditBuf;
		if(testFunction.notNil, {
			testFunction.value(auditBuf);
		});
	}

	inverse_{arg val; inverse = val; this.changed(\criterion, \range); }

	or{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \or);
	}

	and{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \and);
	}

}

AuditBinaryCriterion {
	var a, b;
	var operator; //or, and, xor

	*new{arg a, b, operator = \or;
		^super.newCopyArgs(a, b, operator);
	}

	findQualifiedSegmentIndexes{arg auditBuf;
		var result;
		switch(operator,
			\or, {
				result = a.findQualifiedSegmentIndexes(auditBuf).asSet union:
				b.findQualifiedSegmentIndexes(auditBuf).asSet
			},
			\and, {
				result = a.findQualifiedSegmentIndexes(auditBuf).asSet sect:
				b.findQualifiedSegmentIndexes(auditBuf).asSet
			}
		);
		^result;
	}

	or{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \or);
	}

	and{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \and);
	}

}
