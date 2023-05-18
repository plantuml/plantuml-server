<div class="previewer-container flex-rows">
  <%@ include file="/components/preview/menu/preview-menu.jsp" %>
  <div class="hr"></div>
  <%@ include file="/components/preview/paginator/paginator.jsp" %>
  <div id="paginator" data-number-of-diagram-pages="1" style="display: none;"></div>
  <div class="previewer-main flex-main">
    <%@ include file="/components/preview/diagram/preview-diagram.jsp" %>
  </div>
  <% if (showSocialButtons) { %>
    <div>
      <%@ include file="/components/preview/social-buttons.jsp" %>
    </div>
  <% } %>
  <!-- global modals -->
  <%@ include file="/components/modals/settings/settings.jsp" %>
</div>
