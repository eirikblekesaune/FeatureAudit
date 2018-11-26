AuditFileBuffer : AuditBuffer {

	*new{arg server, path, loadLocalMirFile = false;
		^super.new(server).initAuditFileBuffer(path, loadLocalMirFile);
	}

	initAuditFileBuffer{arg path, loadLocalMirFile;
		if(File.exists(path), {
			soundFile = SoundFile(path);
			soundFile.openRead;
			buffer = Buffer.read(server, path);
			if(loadLocalMirFile, {
				if(this.localMirFileExists, {
					var mirPath = this.localMirFilePath;
					mirFile = SCMIRAudioFile.newFromZ(mirPath);
					features = this.class.prMakeFeatures(mirFile);
				});
			});
		}, {
			"SoundFile not found '%'".format(path).warn;
		});
	}

	//analyzes arg SoundFile object
	//callback arg func exec after analysis done, callback arg is SCMIRAudioFile object
	//args: featureList (array), action;
	// analyze{arg featureArgsList;
	// 	//TODO: check if already existing file
	// 	forkIfNeeded{
	// 		var result;
	// 		var analysisArgs;
	// 		var pathName = PathName(soundFile.path);
	// 		analysisArgs = featureArgsList ? [
	// 			[\Loudness]
	// 		];
	// 		result = SCMIRAudioFile(pathName.fullPath, analysisArgs);
	//
	// 		"Starting analysis of '%'".format(
	// 			pathName.fullPath
	// 		).postln;
	// 		result.extractFeatures();
	// 		result.extractOnsets();
	// 		"\tAnalysis DONE".postln;
	//
	// 		result.save(
	// 			pathName.pathOnly  ++
	// 			pathName.fileNameWithoutExtension ++
	// 			".scmirZ"
	// 		);
	// 		mirFile = result;
	// 		this.prMakeFeatures(analysisArgs);
	// 	}
	// }


}

