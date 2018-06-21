AuditBufferSegment{
	var <startIndex;
	var <numSegments;
	var <buffer;
	var <startTime;
	var <endTime;
	var <duration;
	var event;

	*new{arg startIndex, numSegments, buffer;
		^super.newCopyArgs(startIndex, numSegments, buffer).init;
	}

	init{
		startTime = startIndex * SCMIR.hoptime;
		duration = (numSegments * SCMIR.hoptime);
		endTime = startTime + duration;
		event = (
			bufnum: buffer.bufnum, startSeconds: startTime, dur: duration,
			instrument: "bufplayer%".format(buffer.numChannels).asSymbol
		);
	}

	asEvent{ ^event; }

	printOn { arg stream;
		stream << "AuditBufferSegment(%, %)".format(startIndex, numSegments);
	}

}