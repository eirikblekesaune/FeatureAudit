AuditRangeCriterion : AuditCriterion {
	var <min = 0.0, <max = 1.0;

	*new{arg name, specs, min, max;
		^super.new(name, specs).min_(min).max_(max);
	}

	findQualifiedSegmentIndexes{arg feature;
		var result;
		if(inverse.not, {
			feature.segments.do({arg val, i;
				if((val <= max) && (val >= min), {
					result = result.add(i);
				});
			});
			}, {
				feature.segments.do({arg val, i;
					if(((val <= max) && (val >= min)).not, {
						result = result.add(i);
					});
				});
		});
		^result;
	}

	min_{arg val; min = val; this.changed(\criterion, \range); }
	max_{arg val; max = val; this.changed(\criterion, \range); }
}
