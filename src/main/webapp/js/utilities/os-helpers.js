/*************
* OS Helpers *
**************/

const isMac = (function() {
  const PLATFORM = navigator?.userAgentData?.platform || navigator?.platform || "unknown";
  return PLATFORM.match("Mac");
})();
