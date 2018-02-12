package org.soot;

import java.util.ArrayList;
import java.util.List;

import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.*;
import soot.toolkits.graph.UnitGraph;

public class UnitGraphContainer {

	private UnitGraph graph;
	private List<Unit> visitedUnits = new ArrayList<Unit>();
	
	private Boolean verbose = false;
	
	public UnitGraphContainer(UnitGraph graph) {
		
		this.graph = graph;
	}
	
	
	public void accept(EscapeStatementVisitor visitor) {

		printGraph(graph.getHeads());
		
		this.visitedUnits = new ArrayList<Unit>();

		List<Unit> entryNodes = graph.getHeads();

		for(Unit entryNode : entryNodes) {
			acceptGraphDepthFirst(entryNode, visitor);
		}
		visitor.visitEnd();
	}

	private void printGraph(List<Unit> units) {
		
		for(Unit unit : units) {
			System.out.format("%s\n", unit.toString());
			printGraph(this.graph.getSuccsOf(unit));
		}
	}
	
	private void acceptGraphDepthFirst(Unit unit, EscapeStatementVisitor visitor) {
		
		if(!this.visitedUnits.contains(unit)) {
		
			acceptUnit(unit, visitor);
			this.visitedUnits.add(unit);
			
			List<Unit> successors = this.graph.getSuccsOf(unit);
			
			for(Unit successor : successors) {
				acceptGraphDepthFirst(successor, visitor);
			}
		} else {
			//System.out.println("Circular!");
		}
	}
	

    private void acceptUnit(Unit unit, EscapeStatementVisitor visitor) {
        Stmt stmt = (Stmt) unit;

        // ********************
        // BIG PATTERN MATCHING
        // ********************
        // I throw much "match failure" Errors to ease debugging...
        // => we could optimize the pattern matching a little bit
        
        //G.v().out.println(" | |- exec "+stmt);
        
        ///////////
        // Calls //
        ///////////
        if (stmt.containsInvokeExpr()) {
            //inter.analyseCall(inValue, stmt, outValue);
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
                    
                    visitor.visitAssignment(left.getName(), right.getName());
                    //System.out.format("%s = %s\n", left.getName(), right.getName());
                }

                // v = v[i]
                else if (rightOp instanceof ArrayRef) {
                    Local right = (Local) ((ArrayRef) rightOp).getBase();
                    //outValue.g.assignFieldToLocal(stmt, right, "[]", left);
                }

                // v = v.f
                else if (rightOp instanceof InstanceFieldRef) {
                    Local right = (Local) ((InstanceFieldRef) rightOp).getBase();
                    String field = ((InstanceFieldRef) rightOp).getField().getName();

                    visitor.visitAssignment(left.getName(), right.getName(), field);
                    //System.out.format("%s = %s.%s\n", left.getName(), right.getName(), field);
                }

                // v = C.f
                else if (rightOp instanceof StaticFieldRef) {
                    //outValue.g.localIsUnknown(left);
                	
                	
                	//System.out.format("%s = %s.%s\n", left.getName(), right.getName(), field);
                }

                // v = cst
                else if (rightOp instanceof Constant) {
                    // do nothing...
                }
                
                // v = new / newarray / newmultiarray
                else if (rightOp instanceof AnyNewExpr) {
                	
                	if(rightOp instanceof NewExpr) {
                		NewExpr newExpr = (NewExpr)rightOp;
                		visitor.visitNew(newExpr.getBaseType(), left.getName());
                	}
                }
                
                // v = binary or unary operator
                else if (rightOp instanceof BinopExpr
                        || rightOp instanceof UnopExpr
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
                        //outValue.g.assignLocalToField(right, left, "[]");
                    } else {
                        //outValue.g.mutateField(left, "[]");
                    }
                }
                
                // v[i] = cst
                else if (rightOp instanceof Constant) {
                    //outValue.g.mutateField(left, "[]");
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
                    	
                    	visitor.visitNonStaticFieldAssignment(left.getName(), field, right.getName());
                        //outValue.g.assignLocalToField(right, left, field);
                    } else {
                        //outValue.g.mutateField(left, field);
                    }
                }

                // v.f = cst
                else if (rightOp instanceof Constant) {
                    //outValue.g.mutateField(left, field);
                } else {
                    throw new Error("AssignStmt match failure (rightOp) " + stmt);
                }
            }
            
            // C.f = ...
            else if (leftOp instanceof StaticFieldRef) {
                String field = ((StaticFieldRef) leftOp).getField().getName();

                // C.f = v
                if (rightOp instanceof Local) {
                    Local right = (Local) rightOp;
                    if (right.getType() instanceof RefLikeType) {
                        //outValue.g.assignLocalToStaticField(right, field);
                    } else {
                        //outValue.g.mutateStaticField(field);
                    }
                }

                // C.f = cst
                else if (rightOp instanceof Constant) {
                    //outValue.g.mutateStaticField(field);
                } else {
                    throw new Error("AssignStmt match failure (rightOp) " + stmt);
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
                	visitor.visitParameter(left.getName());
                }
            } else if (rightOp instanceof CaughtExceptionRef) {
                // local =  exception
                //outValue.g.localIsUnknown(left);
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
                //outValue.g.localEscapes(v);
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
        else if (stmt instanceof IfStmt
                || stmt instanceof GotoStmt
                || stmt instanceof LookupSwitchStmt
                || stmt instanceof TableSwitchStmt
                || stmt instanceof MonitorStmt
                || stmt instanceof BreakpointStmt
                || stmt instanceof NopStmt) {
            // do nothing...
        } else {
            throw new Error("Stmt match faliure " + stmt);
        }
    }
    
    public void acceptMethodInvoke(Stmt stmt, EscapeStatementVisitor visitor) {

    	System.out.format("   Call: %s\n", stmt.toString());

    	// m()
    	if(stmt instanceof InvokeStmt) {

    		 InvokeStmt invoke = ((InvokeStmt)stmt);

    		 List<Value> args = invoke.getInvokeExpr().getArgs();
    		 List<String> refArguments = new ArrayList<String>();
    		 
    		 for(Value arg : args) {
    			 
    			 System.out.format("   Arg: %s (%s)\n", arg.toString(), arg.getClass().getName());
    			 
    			 if(arg instanceof Local) {
	                // ignore primitive types
	                if (arg.getType() instanceof RefLikeType) {
	                	refArguments.add(((Local) arg).getName());
	                }    				 
    			 }
    		 }
    		 visitor.visitMethodInvoke(refArguments);
    	}
        /////////////
        // AssignStmt
        /////////////
    	else if(stmt instanceof JAssignStmt) {
    		
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
	            
	            else {
	    			visitor.visitClearLocal(((Local) leftOp).getName());
	            }
            }            
            // v.f = ...
            else if (leftOp instanceof InstanceFieldRef) {
                Local left = (Local) ((InstanceFieldRef) leftOp).getBase();
                String field = ((InstanceFieldRef) leftOp).getField().getName();

            }
            
    		if(rightOp instanceof InvokeStmt) {
    			acceptMethodInvoke((InvokeStmt)rightOp, visitor);
    		}
    		
        } else {
            throw new Error("Stmt match faliure " + stmt);
        }
    }
}
