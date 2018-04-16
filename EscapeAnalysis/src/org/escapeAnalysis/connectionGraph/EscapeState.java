package org.escapeAnalysis.connectionGraph;

public enum EscapeState {
	UNRESOLVED {
		@Override
		public String toString() {
			return "[unresolved]";
		}
	},
	NOESCAPE {
		@Override
		public String toString() {
			return "[noEscape]";
		}
	},
	ESCAPE {
		@Override
		public String toString() {
			return "[escape]";
		}
	};
}
