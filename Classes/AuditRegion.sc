AuditRegion {
	var <name;
	var <buffer;
	var <startFrame;
	var <endFrame;

	*new{arg name, buffer, startFrame, endFrame;
		^super.new.init(name, buffer, startFrame, endFrame);
	}

	*newFromSecs{arg name, buffer, startTime, endTime;
		var startFrame, endFrame;
		//the time is in secs float.
		//this divided by sample rate given how many frames
		startFrame = (startTime * buffer.sampleRate).asInteger;
		endFrame = (endTime * buffer.sampleRate).asInteger;
		^this.new(name, buffer, startFrame, endFrame);
	}

	*newFromReaperMarker{arg reaperMarkerDict, featureBuf;
		var nameField = reaperMarkerDict["Name"].split(Char.space);
		var name = nameField.detect({arg it; "[^@#]".matchRegexp(it, end: 1); });
		var track = nameField.detect({arg it; "@".matchRegexp(it, end: 1); });
		var tags = nameField.select({arg it; "#".matchRegexp(it, end: 1); });
		var startTime, endTime;

		startTime = reaperMarkerDict["Start"].asSecs;
		endTime = reaperMarkerDict["End"].asSecs;
		"nameField: '%'".format(nameField).postln;
		"\tname: '%'".format(name).postln;
		"\ttrack: '%'".format(track).postln;
		"\ttags: '%'".format(tags).postln;
		^this.newFromSecs(
			name,
			featureBuf,
			startTime,
			endTime,
		);
	}

	init{arg name_, buffer_, startFrame_, endFrame_;
		name = name_;
		buffer = buffer_;
		startFrame = startFrame_;
		endFrame = endFrame_;
	}
}