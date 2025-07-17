public class Automaton {
        NFA start; /*start state */
        NFA end;/*final state */
        int numStates;/*number of states in the automaton*/
        String regex;/*regex representing this automaton */
        NFA [] submatches;/*submatches in automaton*/
}
