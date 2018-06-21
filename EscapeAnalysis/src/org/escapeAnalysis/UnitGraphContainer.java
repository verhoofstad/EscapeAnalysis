package org.escapeAnalysis;

import java.util.ArrayList;
import java.util.List;

import soot.ArrayType;
import soot.Local;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.*;
import soot.toolkits.graph.UnitGraph;

/**
 * Traverses a Soot unit graph and calls the appropriate methods of a provided escape statement visitor.
 */
public class UnitGraphContainer {

    private UnitGraph graph;
    private List<Unit> visitedUnits = new ArrayList<Unit>();

    private boolean verbose = false;

    public UnitGraphContainer(UnitGraph graph) {

        this.graph = graph;
    }

    public void accept(EscapeStatementVisitor visitor) {

        // printGraph(graph.getHeads());

        this.visitedUnits = new ArrayList<Unit>();

        List<Unit> entryNodes = graph.getHeads();

        for (Unit entryNode : entryNodes) {
            acceptGraphDepthFirst(entryNode, visitor);
        }
        visitor.visitEnd();
    }

    private void printGraph(List<Unit> units) {

        for (Unit unit : units) {
            System.out.format("%s\n", unit.toString());
            printGraph(this.graph.getSuccsOf(unit));
        }
    }

    private void acceptGraphDepthFirst(Unit unit, EscapeStatementVisitor visitor) {

        if (!this.visitedUnits.contains(unit)) {

            acceptUnit(unit, visitor);
            this.visitedUnits.add(unit);

            List<Unit> successors = this.graph.getSuccsOf(unit);

            for (Unit successor : successors) {
                acceptGraphDepthFirst(successor, visitor);
            }
        } else {
            // System.out.println("Circular!");
        }
    }

    //
    // Copied large chunks from /src/main/java/soot/jimple/toolkits/annotation/purity/PurityIntraproceduralAnalysis.java (Soot)
    //
    private void acceptUnit(Unit unit, EscapeStatementVisitor visitor) {
        Stmt stmt = (Stmt) unit;

        ///////////
        // Calls //
        ///////////
        if (stmt.containsInvokeExpr()) {
            acceptMethodInvoke(stmt, visitor);
        }

        /////////////
        // AssignStmt
        /////////////
        else if (stmt instanceof AssignStmt) {
            Value leftOp = ((AssignStmt) stmt).getLeftOp();
            Value rightOp = ((AssignStmt) stmt).getRightOp();

            // v = ...
            if (leftOp instanceof Local) {
                Local left = (Local) leftOp;

                // remove optional cast
                if (rightOp instanceof CastExpr) {
                    rightOp = ((CastExpr) rightOp).getOp();
                }

                // ignore primitive types
                if (!(left.getType() instanceof RefLikeType)) {
                }

                // v = v
                else if (rightOp instanceof Local) {
                    Local right = (Local) rightOp;

                    println("%s = %s (v = v)", left.getName(), right.getName());
                    visitor.visitAssignment(left.getName(), right.getName());
                }

                // v = v[i]
                else if (rightOp instanceof ArrayRef) {
                    Local right = (Local) ((ArrayRef) rightOp).getBase();

                    // We handle arrays in Java like regular objects, that is, we do not
                    // distinguish between different elements of an array. [Choi et. al. 2003]
                    println("%s = %s[] (v = v[i])", left.getName(), right.getName());
                    visitor.visitAssignment(left.getName(), right.getName());
                }

                // v = v.f
                else if (rightOp instanceof InstanceFieldRef) {
                    Local right = (Local) ((InstanceFieldRef) rightOp).getBase();
                    String field = ((InstanceFieldRef) rightOp).getField().getName();

                    println("%s = %s.%s (v = v.f)", left.getName(), right.getName(), field);
                    visitor.visitAssignment(left.getName(), right.getName(), field);
                }

                // v = C.f
                else if (rightOp instanceof StaticFieldRef) {
                    Type right = ((StaticFieldRef) rightOp).getType();
                    String field = ((StaticFieldRef) rightOp).getField().getName();

                    if (right instanceof RefType) {

                        RefType rightRefType = (RefType) right;

                        println("%s = %s.%s (v = C.f)", left.getName(), rightRefType.getClassName(), field);
                        visitor.visitAssignment(left.getName(), rightRefType, field);

                    } else if (right instanceof ArrayType) {

                        Type rightArrayElementType = ((ArrayType) right).getArrayElementType();

                        if (rightArrayElementType instanceof RefType) {
                            println("%s = %s[].%s (v = C[].f)", left.getName(),
                                    ((RefType) rightArrayElementType).getClassName(), field);
                            visitor.visitAssignment(left.getName(), (RefType) rightArrayElementType, field);
                        }
                    } else {
                        throw new Error("Unhandled type. " + right);
                    }
                }

                // v = cst
                else if (rightOp instanceof Constant) {
                    // do nothing...
                }

                // v = new / newarray / newmultiarray
                else if (rightOp instanceof AnyNewExpr) {

                    if (rightOp instanceof NewExpr) {
                        NewExpr newExpr = (NewExpr) rightOp;

                        println("%s = new %s() (v = new T())", left.getName(), newExpr.getBaseType().getClassName());
                        visitor.visitNew(newExpr.getBaseType(), left.getName());
                    } else if (rightOp instanceof NewArrayExpr) {
                        NewArrayExpr newExpr = (NewArrayExpr) rightOp;

                        if (newExpr.getBaseType() instanceof RefType) {
                            RefType baseType = (RefType) newExpr.getBaseType();

                            println("%s = new %s[] (v =  T[])", left.getName(), baseType.getClassName());
                            visitor.visitNew(baseType, left.getName());
                        }
                    } else if (rightOp instanceof NewMultiArrayExpr) {
                        ArrayType arrayType = ((NewMultiArrayExpr) rightOp).getBaseType();

                        if (arrayType.baseType instanceof RefType) {
                            RefType baseType = (RefType) arrayType.baseType;
                            int dimensions = arrayType.numDimensions;

                            println("%s = new %s%s (v =  T[])", left.getName(), baseType.getClassName(),
                                    new String(new char[dimensions]).replace("\0", "[]"));
                            visitor.visitNew(baseType, left.getName());
                        }
                    }
                }

                // v = binary or unary operator
                else if (rightOp instanceof BinopExpr || rightOp instanceof UnopExpr
                        || rightOp instanceof InstanceOfExpr) {
                    // do nothing...
                } else {
                    throw new Error("AssignStmt match failure (rightOp)" + stmt);
                }
            }

            // v[i] = ...
            else if (leftOp instanceof ArrayRef) {
                Local left = (Local) ((ArrayRef) leftOp).getBase();

                // v[i] = v
                if (rightOp instanceof Local) {
                    Local right = (Local) rightOp;

                    if (right.getType() instanceof RefLikeType) {

                        println("%s[] = %s (v[i] = v)", left.getName(), right.getName());
                        visitor.visitAssignment(left.getName(), right.getName());
                    } else if (right.getType() instanceof PrimType) {
                        // Ignore
                    } else {
                        throw new Error("Unhandled right hand. " + right.getType().getClass().getName());
                    }
                }

                // v[i] = cst
                else if (rightOp instanceof Constant) {
                    println("%s[] = (const)", left.getName());
                    visitor.visitClearLocal(left.getName());
                } else {
                    throw new Error("AssignStmt match failure (rightOp)" + stmt);
                }
            }

            // v.f = ...
            else if (leftOp instanceof InstanceFieldRef) {
                Local left = (Local) ((InstanceFieldRef) leftOp).getBase();
                String field = ((InstanceFieldRef) leftOp).getField().getName();

                // v.f = v
                if (rightOp instanceof Local) {
                    Local right = (Local) rightOp;

                    // ignore primitive types
                    if (right.getType() instanceof RefLikeType) {

                        println("%s.%s = %s (v.f = v)", left.getName(), field, right.getName());
                        visitor.visitNonStaticFieldAssignment(left.getName(), field, right.getName());
                    }
                }

                // v.f = cst
                else if (rightOp instanceof Constant) {

                    visitor.visitClearField(left.getName(), field);
                } else {
                    throw new Error("AssignStmt match failure (rightOp) " + stmt);
                }
            }

            // C.f = ...
            else if (leftOp instanceof StaticFieldRef) {

                Type left = ((StaticFieldRef) leftOp).getType();
                String field = ((StaticFieldRef) leftOp).getField().getName();

                if (left instanceof RefType) {

                    // C.f = v
                    if (rightOp instanceof Local) {
                        Local right = (Local) rightOp;
                        if (right.getType() instanceof RefLikeType) {

                            println("%s.%s = %s (C.f = v)", ((RefType) left).getClassName(), field, right.getName());
                            visitor.visitStaticFieldAssignment((RefType) left, field, right.getName());
                        } else {
                            // outValue.g.mutateStaticField(field);
                            throw new Error("Unhandled right hand." + right);
                        }
                    }

                    // C.f = cst
                    else if (rightOp instanceof Constant) {
                        // outValue.g.mutateStaticField(field);
                    } else {
                        throw new Error("AssignStmt match failure (rightOp) " + stmt);
                    }

                } else if (left instanceof ArrayType) {

                } else if (left instanceof PrimType) {
                    // Ignore
                } else {
                    throw new Error("Unhandled left type. " + left.getClass().getName());
                }

            } else {
                throw new Error("AssignStmt match failure (leftOp) " + stmt);
            }
        }

        ///////////////
        // IdentityStmt
        ///////////////
        else if (stmt instanceof IdentityStmt) {
            Local left = (Local) ((IdentityStmt) stmt).getLeftOp();
            Value rightOp = ((IdentityStmt) stmt).getRightOp();

            if (rightOp instanceof ThisRef) {
                visitor.visitThis(left.getName());
            } else if (rightOp instanceof ParameterRef) {
                ParameterRef p = (ParameterRef) rightOp;
                // ignore primitive types
                if (p.getType() instanceof RefLikeType) {
                    println("param: %s", left.getName());
                    visitor.visitParameter(left.getName());
                }
            } else if (rightOp instanceof CaughtExceptionRef) {
                // local = exception
                // outValue.g.localIsUnknown(left);
            } else {
                throw new Error("IdentityStmt match failure (rightOp) " + stmt);
            }
        }

        ////////////
        // ThrowStmt
        ////////////
        else if (stmt instanceof ThrowStmt) {
            Value op = ((ThrowStmt) stmt).getOp();

            if (op instanceof Local) {
                Local v = (Local) op;

                // A throw statement is handled in the same manner as a return statement.
                println("throw %s", v.getName());
                visitor.visitReturn(v.getName());
            } else if (op instanceof Constant) {
                // do nothing...
            } else {
                throw new Error("ThrowStmt match failure " + stmt);
            }
        }

        /////////////
        // ReturnStmt
        /////////////
        else if (stmt instanceof ReturnVoidStmt) {
            // do nothing...
        } else if (stmt instanceof ReturnStmt) {
            Value v = ((ReturnStmt) stmt).getOp();

            if (v instanceof Local) {
                // ignore primitive types
                if (v.getType() instanceof RefLikeType) {
                    println("return %s", ((Local) v).getName());
                    visitor.visitReturn(((Local) v).getName());
                }
            } else if (v instanceof Constant) {
                // do nothing...
            } else {
                throw new Error("ReturnStmt match failure " + stmt);
            }
        }

        //////////
        // ignored
        //////////
        else if (stmt instanceof IfStmt || stmt instanceof GotoStmt || stmt instanceof LookupSwitchStmt
                || stmt instanceof TableSwitchStmt || stmt instanceof MonitorStmt || stmt instanceof BreakpointStmt
                || stmt instanceof NopStmt) {
            // do nothing...
        } else {
            throw new Error("Stmt match faliure " + stmt);
        }
    }

    public void acceptMethodInvoke(Stmt stmt, EscapeStatementVisitor visitor) {

        // System.out.format(" Call: %s\n", stmt.toString());

        // m()
        if (stmt instanceof InvokeStmt) {
            HandleMethodInvoke(((InvokeStmt) stmt).getInvokeExpr(), visitor);
        }
        /////////////
        // AssignStmt
        /////////////
        else if (stmt instanceof JAssignStmt) {

            Value leftOp = ((AssignStmt) stmt).getLeftOp();
            Value rightOp = ((AssignStmt) stmt).getRightOp();

            // v = ...
            if (leftOp instanceof Local) {
                Local left = (Local) leftOp;

                // remove optional cast
                if (rightOp instanceof CastExpr) {
                    rightOp = ((CastExpr) rightOp).getOp();
                }

                // ignore primitive types
                if ((left.getType() instanceof RefLikeType)) {
                    visitor.visitClearLocal(((Local) leftOp).getName());
                }
            }
            // v.f = ...
            else if (leftOp instanceof InstanceFieldRef) {
                Local left = (Local) ((InstanceFieldRef) leftOp).getBase();
                String field = ((InstanceFieldRef) leftOp).getField().getName();

                visitor.visitClearField(left.getName(), field);
            } else {
                throw new Error("Unhandled leftOp in JAssignStmt " + leftOp.getClass().getName());
            }

            if (rightOp instanceof InvokeStmt) {
                HandleMethodInvoke(((InvokeStmt) rightOp).getInvokeExpr(), visitor);
            } else if (rightOp instanceof StaticInvokeExpr) {
                HandleMethodInvoke((InvokeExpr) rightOp, visitor);
            } else if (rightOp instanceof VirtualInvokeExpr) {
                HandleMethodInvoke((InvokeExpr) rightOp, visitor);
            } else if (rightOp instanceof InterfaceInvokeExpr) {
                HandleMethodInvoke((InvokeExpr) rightOp, visitor);
            } else if (rightOp instanceof DynamicInvokeExpr) {
                // Ignore dynamic invoke
            } else if (rightOp instanceof SpecialInvokeExpr) {
                HandleMethodInvoke((InvokeExpr) rightOp, visitor);
            } else {
                throw new Error("Unhandled rightOp in JAssignStmt " + rightOp.getClass().getName());
            }

        } else {
            throw new Error("Stmt match faliure " + stmt);
        }
    }

    private void HandleMethodInvoke(InvokeExpr invokeExpr, EscapeStatementVisitor visitor) {

        List<Value> args = invokeExpr.getArgs();
        List<String> refArguments = new ArrayList<String>();

        for (Value arg : args) {

            // println(" Arg: %s (%s)\n", arg.toString(), arg.getClass().getName());

            if (arg instanceof Local) {
                // ignore primitive types
                if (arg.getType() instanceof RefLikeType) {
                    refArguments.add(((Local) arg).getName());
                }
            }
        }
        visitor.visitMethodInvoke(refArguments);
    }

    private void println(String format, Object... args) {
        if (this.verbose) {
            System.out.format(format + "\n", args);
        }
    }
}