+ Array {
	clumpConsecutive {
		var result = [];
		var temp;
		this.doAdjacentPairs({arg aa, bb, i;
			if(i == 0, {
				temp = [aa];
			});
			if(bb == (aa + 1), {
				temp = temp.add(bb);
			}, {
				result = result.add(temp);
				temp = [bb];
			});
			if(i == (this.size - 2), {
				result = result.add(temp);
			});
		});
		^result;
	}

	consecutiveToRanges {
		var result;
		this.segmentConsecutiveFrames.collect({arg segment;
			result = result.add(
				Range(
					segment.first,
					segment.last - segment.first
				)
			);
		});
		^result;
	}
}
