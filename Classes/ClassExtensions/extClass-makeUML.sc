+ Class {

	makeUML{arg outputFilepath, includeSubclasses = false, openWhenDone = true;
        var result;
        var classes;
        classes = [this];
        if(includeSubclasses, {
            classes = classes ++ this.allSubclasses;
        });
        result = this.makeUMLForClasses(classes, outputFilepath, openWhenDone);
        ^result;
    }

    makeUMLForClasses{arg classes, outputFilepath, openWhenDone = true;
        var result;
		var dotfilePath = outputFilepath ?? {
            "~/Desktop/%_UML.dot".format(this.name).standardizePath};
		var dotfile;

		dotfile = File.new(dotfilePath, "w");
		if(dotfile.isOpen.not, {
			"Failed opening file".error.throw;
		});

        dotfile << this.makeDotfileStringForClasses(classes);

		dotfile.close();
		while({dotfile.isOpen}, {
			".".post;
		});
        result = this.prCompileDotfile(dotfilePath, openWhenDone);
        ^result; //returns nil if compile failed. Returns pdf filepath is success.
    }
    
    makeDotfileStringForClasses{arg classes;
        var result;
		result = result ++ "digraph G {" ++ "\n";
		result = result ++ "\tfontname = \"Bitstream Vera Sans\"" ++ "\n";
		result = result ++ "\tfontsize = 8" ++ "\n";
		result = result ++ "\n";
		result = result ++ "\trankdir = \"LR\"\n";
		result = result ++ "\tnode [" ++ "\n";
		result = result ++ "\t\tfontname = \"Bitstream Vera Sans\"" ++ "\n";
		result = result ++ "\t\tfontsize = 8" ++ "\n";
		result = result ++ "\t\tshape = \"record\"" ++ "\n";
		result = result ++ "\t]" ++ "\n";
		result = result ++ "\n";
		result = result ++ "\tedge [" ++ "\n";
		result = result ++ "\t\tfontname = \"Bitstream Vera Sans\"" ++ "\n";
		result = result ++ "\t\tfontsize = 8" ++ "\n";
		result = result ++ "\t]" ++ "\n";
		result = result ++ "\n";

        classes.do({arg cls;
            result = result ++ cls.prMakeDotfileStringForClass();
        });
		//draw connections
		classes.do({arg class;
			if(classes.includes(class.superclass), {
				result = result ++ "% -> % [dir=back]\n".format(
                    class.superclass.name, class.name);
			});
		});

		result = result ++ "}" ++ "\n";

        ^result;
    }

    prMakeDotfileStringForClass{
        var result = "";
        //The class specific dotfile string is generated here
        result = result ++ "\t% [".format(this.name) ++ "\n";
        result = result ++ "\t\tlabel = ";
        result = result ++ "\"{%|%|%}\"".format(
            this.name,
            this.getInstVarDotString(),
            this.getMethodDotStringForClass()
        );
        result = result ++ "\n";
        result = result ++ "\t]" ++ "\n";
        ^result;
    }

    prWriteDotfile{arg filepath;
    
    }

    prCompileDotfile{arg dotfilePath, openWhenDone;
        var result;
		"/usr/local/bin/dot % -Tpdf -O".format(dotfilePath).unixCmd({arg returnCode ...args;
			var imgPath;
			if(returnCode != 0, {
				"Error running 'dot' program".warn;
			}, {
				imgPath = dotfilePath ++ ".pdf";
                if(openWhenDone, {
                    "open %".format(imgPath).postln.unixCmd;
                });
                result = imgPath;
			});
		});
        ^result;
    }
	
    getInstVarDotString {
        var result;
        this.classVarNames.do({arg instVarName;
            result = result ++ "% %\\l".format(
                "*", //getset
                instVarName
            );
        });
        // cls.privateInstVars.do({arg instVarName;
        // 	result = result ++ "% %\\l".format(
        // 		"-", //getset
        // 		instVarName
        // 	);
        // });
        this.instVars.do({arg instVarName;
            result = result ++ "% %\\l".format(
                "-", //getset
                instVarName
            );
        });
        ^result;
    }

    getMethodDotStringForClass {
        var result;
        this.class.methods.do({arg it;
            var args = "";
            if(it.argNames.size > 1, {
                args = it.argumentString;
            });
            result = result ++ "* %( % )\\l".format(
                this.escapeCharsForDotfile(it.name.asString),
                this.escapeCharsForDotfile(args)
            );
            this.escapeCharsForDotfile(result);
        });
        this.methods.reject({arg it;
            it.hasCorrespondingInstVar;
        }).do({arg it;
            var args = "";
            if(it.argNames.size > 1, {
                args = it.argumentString;
            });
            result = result ++ "%( % )%%\\l".format(
                this.escapeCharsForDotfile(it.name.asString),
                this.escapeCharsForDotfile(args),
                if(it.isOverridingSuperclass, {
                    " @overrides %".format(it.findOverriddenMethod.ownerClass.name)
                }, {
                    "";
                }),
                if(it.isExtensionMethod, {
                    " @extension"
                }, {
                    "";
                })
            );
        });
        ^result;
    }

    escapeCharsForDotfile{arg str;
        var result = str;
        [$\\, $", $>, $<, $|].do({arg it;
            result = result.escapeChar(it);
        });
        ^result;
    }

	dotUMLString{
		var str;
		str = "\t% [".format(this.name) << "\n";
		str = str ++ "\t\tlabel = ";
		str = str ++ "\"{%|%|%}\"".format(
			this.name,
			this.instVarDotUMLString,
			this.methodDotUMLString
		);
		str = str ++ "\n";
		str = str ++ "\t]" << "\n";
		^str;
	}

	instVarDotUMLString{
		var result;
		this.classVarNames.do({arg instVarName;
			result = result ++ "% %\\l".format(
				"*", //getset
				instVarName
			);
		});

		this.instVars.do({arg instVarName;
			result = result ++ "% %\\l".format(
				"-", //getset
				instVarName
			);
		});
		^result;
	}

	methodDotUMLString{
		var result;
		var escapeChars = {arg str;
			var result = str;
			[$\\, $", $>, $<, $|].do({arg it;
				result = result.escapeChar(it);
			});
			result;
		};
		this.methods.do({arg it;
			var args = "";
			if(it.argNames.size > 1, {
				args = it.argumentString;
			});
			result = result ++ "* %( % )\\l".format(
				escapeChars.value(it.name.asString),
				escapeChars.value(args)
			);
			escapeChars.value(result);
		});

		this.methods.reject({arg it;
			it.hasCorrespondingInstVar;
		}).do({arg it;
			var args = "";
			if(it.argNames.size > 1, {
				args = it.argumentString;
			});
			result = result ++ "%( % )%%\\l".format(
				escapeChars.value(it.name.asString),
				escapeChars.value(args),
				if(it.isOverridingSuperclass, {
					" @overrides %".format(
						it.findOverriddenMethod.ownerClass.name)
				}, {
					"";
				}),
				if(it.isExtensionMethod, {
					" @extension"
				}, {
					"";
				})
			);
		});
		^result;
	}
}
