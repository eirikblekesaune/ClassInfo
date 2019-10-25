# ClassInfo
Methods for analyzing classes, methods in sclang in SuperCollider

You can e.g. generate a PDF file with a graphical layout of classes and their inheritance with this line:
```syntax=supercollider
(
Class.makeUMLForClasses(
[UGen, UGen.allSubclasses].flat,
"~/Desktop/UGen".standardizePath);
)
```
