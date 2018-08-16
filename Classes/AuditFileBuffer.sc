AuditFileBuffer : AuditBuffer {

  *new{arg server, path, loadLocalMirFile = false;
    ^super.new(server).initAuditFileBuffer(path, loadLocalMirFile);
  }

  initAuditFileBuffer{arg path, loadLocalMirFile;
    var tempAnalysisArgs  = [ // just a cheat for fixes
      [\Loudness],
      [\SensoryDissonance],
      [\SpecCentroid],
      [\SpecPcile, 0.90],
      [\SpectralEntropy],
      [\SpecFlatness],
      [\Onsets],
      [\FFTSlope],
      [\FFTCrest],
      [\FFTSpread],
      [\Tartini, 2],
      [\KeyClarity]
    ];
    if(File.exists(path), {
      soundFile = SoundFile(path);
      soundFile.openRead;
      buffer = Buffer.read(server, path);
      if(loadLocalMirFile, {
        var pn = PathName(path);
        var mirPath = pn.pathOnly ++ pn.fileNameWithoutExtension ++ ".scmirZ";
        if(File.exists(mirPath), {

          mirFile = SCMIRAudioFile.newFromZ(mirPath);
          features = this.class.prMakeFeatures(mirFile, tempAnalysisArgs);
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

