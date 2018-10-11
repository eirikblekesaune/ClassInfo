+ Method{
	isDefined{
		^this.prototypeFrame.notNil;
	}

	isSynthesized{
		^this.isDefined.not;
	}

	isOverridingSuperclass{
		^this.findOverriddenMethod.notNil;
	}

	findOverriddenMethod{
		^this.ownerClass.superclass.findRespondingMethodFor(this.name);
	}

	hasCorrespondingInstVar{
		var methName = this.name;
		if(this.name.isSetter, {
			methName = methName.asGetter;
		});
		^this.ownerClass.instVarNames.as(Set).includes(methName);
	}

	isExtensionMethod{
		var result = false;
		result = this.filenameSymbol != this.ownerClass.filenameSymbol;
		^result;
	}

	// overrideChain{
	// 	var findNext;
	// 	findNext = {arg cls;
	// 		var nextMeth = cls.findOverriddenMethod(this.name);
	// 		if(nextMeth.notNil, {
	// 			result = result.add(findNext.value(this.ownerClass.superclass);
	// 			}, {
	//
	// 			});
	// 			result;
	// 		};
	// 		^findNext.value(this.ownerClass.superclass);
	// 	}

}

