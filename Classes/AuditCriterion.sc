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

	findQualifiedSections{arg feature;
		var result, segIndexes;
		segIndexes = this.findQualifiedSegmentIndexes(feature);
		segIndexes = segIndexes.clumpConsecutive;
		result = segIndexes.collect({arg indexes, i; 
			AuditSection(indexes.first, indexes.last);
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

	findQualifiedSegmentIndexes{arg feature;
		var result;
		switch(operator,
			\or, {
				result = a.findQualifiedSegmentIndexes(feature).asSet union:
				b.findQualifiedSegmentIndexes(feature).asSet
			},
			\and, {
				result = a.findQualifiedSegmentIndexes(feature).asSet sect:
				b.findQualifiedSegmentIndexes(feature).asSet
			}
		);
		^result.asArray.sort;
	}

	findQualifiedSections{arg feature;
		var result;
		switch(operator,
			\or, {
				result = a.findQualifiedSections(feature).asSet union:
				b.findQualifiedSections(feature).asSet
			},
			\and, {
				result = a.findQualifiedSections(feature).asSet sect:
				b.findQualifiedSections(feature).asSet
			}
		);
		^result.asArray.sort;
	}

	or{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \or);
	}

	and{arg aCriterion;
		^AuditBinaryCriterion(this, aCriterion, \and);
	}

}
