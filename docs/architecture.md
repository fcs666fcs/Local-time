# Architecture
feature -> domain -> data/core. core does not depend on Android Context. UI observes state and renders. Non-critical network work is delayed until after the first interactive frame.
