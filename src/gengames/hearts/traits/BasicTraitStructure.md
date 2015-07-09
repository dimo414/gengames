Default Traits

* Discard lowest
* '' highest
* discard smallest suit
* '' largest suit
* play spades low
* '' high
* play hearts low
* '' high
* play random

Each Gene Is A Time In Play:
Set of things to track for the set of normal traits

* **Starting Trick vs. In Play**               S/P
* **Hearts Broken vs. Hearts Not Broken**      B/N
* **Queen Played vs. Not Played**              P/N
* **Queen Is/Was Ours vs. Elsewhere**          H/E
  
    maybe tracking where exactly it is

* **Opponent Could Shoot The Moon vs. Safe**   M/S

    danger is if a player has taken the queen and
    hearts hasn't broken / they've taken all the
    hearts thusfar and the queen - not sure how to track
  
* **Took Queen vs. Hasn't Taken Queen**        Q/N
    
Looks like there will be 64 combinations
Here are the possible combinations:

    SBPHSQ
    SBPESN
    SBPESQ
    SBPEMN
    SBPESN
    SBNHSN
    SBNHMN
    SBNESN
    SBNEMN
    SNPHSQ
    SNPEMN
    SNNHMN
    SNNEMN
    PBPHSQ
    PBPESN
    PBPHSQ
    PBPEMN
    PBPESN
    PNPHSQ
    PNPEMN
    ...
    SBPHSN
    SBPESN
    SBPHMN
    SNPESQ
    SNPHMN
    PBPHSQ
    PBPESQ
    PBPHSN
    PBPESN
    PBPHSQ
    PBPESQ
    PBPHMN
    PBPHSN
    PBPESN
    PNPESQ
    PNPHMN
    PNNHMN
    PNNEMN
    PNNHMN
    PNNEMN

---

    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
    
    SNPHMN
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Hasn't Taken Queen
        
    SBPHSQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Safe
        Took Queen
    
    SBPHSN
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Safe
        Hasn't Taken Queen
        
    SBPEMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Elsewhere
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPEMN
        Starting Trick
        Hearts Broken
        Queen Played
        Elsewhere
        Opponent Could Shoot The Moon
        Hasn't Taken Queen
        
    SBPESQ
        Starting Trick
        Hearts Broken
        Queen Played
        Elsewhere
        Safe
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen
        
    SBPHMQ
        Starting Trick
        Hearts Broken
        Queen Played
        Queen Is/Was Ours
        Opponent Could Shoot The Moon
        Took Queen