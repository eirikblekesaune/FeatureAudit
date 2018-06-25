AuditCriterion{
	var <name;
	var <>specs;
	var <>active = true;
	var <inverse = false;
	var >testFunction;

	*new{arg name, specs;
		^super.newCopyArgs( name, specs );
	}

	findQualifiedSegmentIndexes{arg feature;
		var result;
		if(testFunction.notNil, {
			feature.segments.do({arg val, i;
				if(testFunction.value(val, i), {
					result = result.add(i);
				});
			});
		});
		^result;
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
