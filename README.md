# Basically Publish-Subscribe + Query Expansion
Uses [DSOL](https://simulation.tudelft.nl/dsol/docs/latest/documentation.html) as base event-driven simulation. 
Planning to use [TREC-Microblog datasets](https://github.com/ngc7023/TREC-Microblog-Datasets/tree/master) for the event messages. Will be updating this.

```mermaid
flowchart TD
P[Publisher
Event Producer]
B[Broker
Event Listener + Event Producer]
S[Subscribers
EventListener]

P -->|MESSAGE_EVENT| B


subgraph Broker Processing
    F1[Metadata Prefilter]
    F2[Query Expansion]
    F3[Semantic Matching]
    F1 --> F2 --> F3
end

B --> F1
F3 --> S
```