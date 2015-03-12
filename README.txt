
Implementation of a min/max spinner that consists of two
interconnected spinners.  Each has its own addapter and they
use the same list.  The max spinner index is not allowed to
select anything below the min spinner.

3/9/2015
modified so same code would work with fragments or activities
and added demo code.

Wed Mar 11 19:33:36 PDT 2015
Had problems with reset on orientation change and fixed that.
Problems mostly came from the fact that I was changing
the array lists that the adapter used.  I expected it
to make a safe copy -- but it doesn't.  Also I had to change
the way the includes worked in the .xml files and save the
'currentIndex' between restarts from the orientation change
because the adapter on the fragment (not the activity) reset
this.

Update...
Gave up on getting hint to work.  It behaves differently on 
fragment than activity and calls the onListen in one case...
Anyway, I set min ti the min value and max to the max value
and am calling it good.

