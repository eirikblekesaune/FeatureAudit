AuditRegion {
	var <name;
	var <auditBuf;
	var <startFrame;
	var <endFrame;
	var <tags;
	var <track;
	var channelPeaks;
	var normalizeFactor;

	*new{arg name, auditBuf, startFrame, endFrame, tags, track, channelPeaks;
		^super.new.init(name, auditBuf, startFrame, endFrame, tags, track, channelPeaks);
	}

	*newFromSecs{arg name, auditBuf, startTime, endTime, tags, track, channelPeaks;
		var startFrame, endFrame;
		//the time is in secs float.
		//this divided by sample rate given how many frames
		startFrame = (startTime * auditBuf.sampleRate).asInteger;
		endFrame = (endTime * auditBuf.sampleRate).asInteger;
		^this.new(name, auditBuf, startFrame, endFrame, tags, track, channelPeaks);
	}

	*newFromReaperMarker{arg reaperMarkerDict, featureBuf;
		var result;
		var nameField = reaperMarkerDict["Name"].split(Char.space);
		var name = nameField.detect({arg it; "[^@#]".matchRegexp(it, end: 1); });
		var track = nameField.detect({arg it; "@".matchRegexp(it, end: 1); });
		var tags = nameField.select({arg it; "#".matchRegexp(it, end: 1); });
		var startTime, endTime;

		startTime = reaperMarkerDict["Start"].asSecs;
		endTime = reaperMarkerDict["End"].asSecs;

		result = this.newFromSecs(
			name,
			featureBuf,
			startTime,
			endTime,
			tags,
			track
		);
		^result;
	}

	init{arg name_, auditBuf_, startFrame_, endFrame_, tags_, track_, channelPeaks_;
		name = name_;
		auditBuf = auditBuf_;
		startFrame = startFrame_;
		endFrame = endFrame_;
		tags = tags_ ? [];
		track = track_;
		channelPeaks = channelPeaks_;
	}

	numFrames{
		^(endFrame - startFrame);
	}

	duration {
		^(this.numFrames / auditBuf.sampleRate);
	}

	startTime{
		^(startFrame /  auditBuf.sampleRate);
	}

	endTime{
		^(endFrame /  auditBuf.sampleRate);
	}

	channelPeaks{arg chunkSize = 1048576, threaded = false;
		var result;
		if(channelPeaks.notNil, {
			^channelPeaks;
		});
		result = auditBuf.channelPeaks(
			this.startFrame, this.endFrame - this.startFrame,
			chunkSize, threaded
		);
		channelPeaks = result;
		^result;
	}

	normalizeFactor{arg findPeaksIfNil = false, chunkSize = 1048576, threaded = false;
		if(channelPeaks.notNil, {
			^normalizeFactor = channelPeaks.maxItem.reciprocal;
		}, {
			if(findPeaksIfNil, {
				this.channelPeaks(chunkSize, threaded);
				^this.normalizeFactor;
			}, {
				"AuditRegion:normalizeFactor - channelPeaks not calculated yet".warn;
				^1.0;
			});
		});
	}

	bufnum{ ^auditBuf.bufnum; }

	play {arg server, mul = 0.1, loop = false, normalize = false;
		var normFactor = 1.0;
		if(normalize, {
			normFactor = this.normalizeFactor;
		});
		^{
			var player;
			var endFrameTrigger = TDuty.ar(this.duration);
			player = PlayBuf.ar(
				auditBuf.numChannels,
				auditBuf.bufnum,
				BufRateScale.kr(auditBuf.bufnum),
				endFrameTrigger * loop.binaryValue,
				startPos: this.startFrame,
				loop: loop.binaryValue
			);
			if(loop.not, {
				FreeSelf.kr(TDelay.kr(Impulse.kr(0), this.duration));
			});
			player * mul * normFactor;
		}.play(server ? Server.default);
	}
}