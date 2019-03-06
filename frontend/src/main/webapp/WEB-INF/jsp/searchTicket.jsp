<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <title>Search Tickets</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"/>
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <%--autocomplete--%>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <%--multiselect--%>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css"/>
    <%--datepicket--%>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.min.css"/>
    <script
            src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
    <style>
        .btn-info {
            background-color: #62CCE8;
            outline: none;
            color: #F62CCE8;
            border-color: #59B2E6;
        }

        .btn-info:hover,
        .btn-info:focus {
            color: #000000;
            background-color: #62CCE8;
            border-color: #62CCE8;
        }

        .container {
            margin-top: 50px;
        }

        .navigation {
            background-color: #f5821f;
            position: fixed;
            left: 0;
            right: 0;
            top: 0px;
            z-index: 9999;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .navigation .navigation-brand {
            color: white;
            font-family: monospace;
            font-size: 20px;
            margin-left: 20px;
        }

        .navigation-action-button {
            margin-right: 20px;
        }
        .glyphicon-search,
        .glyphicon-user {
            margin-right: 20px;
        }
    </style>
</head>
<body>
<br/><br/>
<div class="container" align="left" style="width:600px;">
    <div class="navigation">
        <span class="navigation-brand">T!ckTrack</span>

        <div class="navigation-action-button">
            <span class="glyphicon glyphicon-search"></span>
            <span class="glyphicon glyphicon-user"></span>
            <span class="glyphicon glyphicon-log-out"></span>
        </div>
    </div>
    <h4 align="left">Select filters to search tickets</h4>
    <br/><br/>
    <form method="post" id="search_form" action="searchTickets">
        <div class="form-group">
            <input type="text" class="form-control" id="summaryOrDescription"
                   placeholder="Keyword to search in Summary or Description">
        </div>
        <div class="form-group">
            <input type="text" class="form-control" id="ticket_id"
                   placeholder="Ticket ID">
        </div>
        <div class="form-group">
            <div class="ui-widget">
                <input type="text" class="form-control" id="assignee"
                       placeholder="Assignee">
            </div>
        </div>
        <div class="form-group">
            <select id="group" name="group[]" multiple class="form-control">
                <c:forEach var="group" items="${groupList}">
                    <option value="${group}">
                            ${group}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <input type="text" class="form-control" id="creator"
                   placeholder="Creator">
        </div>
        <div class="form-group">
            <input type="text" class="form-control" id="resolution"
                   placeholder="Resolution contains...">
        </div>
        <div class="form-group">
            <select id="priority" name="priority[]" multiple class="form-control">
                <option value="Low">Low</option>
                <option value="Medium">Medium</option>
                <option value="High">High</option>
                <option value="Critical">Critical</option>
            </select>
        </div>
        <div class="form-group">
            <select id="status" name="status[]" multiple class="form-control">
                <option value="Open">Open</option>
                <option value="Assigned">Assigned</option>
                <option value="In_Progress">In Progress</option>
                <option value="Resolved">Resolved</option>
                <option value="Canceled">Canceled</option>
                <option value="Closed">Closed</option>
            </select>
        </div>
        <div class="form-group">
            <select id="category" name="category[]" multiple class="form-control">
                <c:forEach var="category" items="${categoryList}">
                    <option value="${category}">
                            ${category}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Open date</label>
            <div class="input-daterange input-group" id="openDate">
                <input type="text" class="input-sm form-control" name="openDateStart"/>
                <span class="input-group-addon">to</span>
                <input type="text" class="input-sm form-control" name="openDateEnd"/>
            </div>
        </div>
        <label>Close date</label>
        <div class="form-group">
            <div class="input-daterange input-group" id="closeDate">
                <input type="text" class="input-sm form-control" name="closeDateStart"/>
                <span class="input-group-addon">to</span>
                <input type="text" class="input-sm form-control" name="closeDateEnd"/>
            </div>
        </div>
        <div class="form-group">
            <label>Deadline</label>
            <div class="input-daterange input-group" id="deadline">
                <input type="text" class="input-sm form-control" name="deadlineStart"/>
                <span class="input-group-addon">to</span>
                <input type="text" class="input-sm form-control" name="deadlineEnd"/>
            </div>
        </div>
        <div class="form-group">
            <input type="submit" class="btn btn-info" name="submit" value="Search"/>
        </div>
    </form>
    <br/>
</div>
</body>
</html>

<script>
    $(document).ready(function () {
        $('#priority').multiselect({
            nonSelectedText: 'Priority',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '200px'
        });
        $('#group').multiselect({
            nonSelectedText: 'Group',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '200px'
        });
        $('#status').multiselect({
            nonSelectedText: 'Status',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '200px'
        });
        $('#category').multiselect({
            nonSelectedText: 'Category',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '200px'
        });
        $('.input-daterange').datepicker({
            todayBtn: "linked",
            clearBtn: true,
            daysOfWeekHighlighted: "0",
            todayHighlight: true,
            format: 'yyyy-mm-dd'
        });
        var cache = {};
        $('#assignee, #creator').autocomplete({
            minLength: 1,
            source: function (request, response) {
                var term = request.term;
                if (term in cache) {
                    response(cache[term]);
                    return;
                }

                $.getJSON("searchUsers", request, function (data, status, xhr) {
                    cache[term] = data;
                    response(data);
                });
            }
        });
    });
</script>