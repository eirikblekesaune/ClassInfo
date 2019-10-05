+ Class {
    getAllClassesInFolder{arg path, ignoreMetaClasses = true;
        var result;
        result = this.allClasses.select({arg class;
            "%.+".format(path).matchRegexp(class.filenameSymbol.asString);
        });

        if(ignoreMetaClasses, {
            result = result.reject({arg class; class.isMetaClass});
        });
        ^result;
    }
}
