<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Create new Ticket</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>

    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-3-typeahead/4.0.2/bootstrap3-typeahead.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css"/>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.min.css"/>
    <script
            src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style>
        .sidnav {
            height: 200%;
            position: absolute;
            right: 86%;
            left: 0;
            top: 34px;
            background-color: #474c55;
        }

        .navigation {
            background-color: #f5821f;
        <c:if test="${admin}"> background-color: brown;
        </c:if> position: fixed;
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

        .sidnav-item-conteiner {
            border-bottom: 1px solid white;
            padding: 20px;
        }

        .sidnav-item {
            text-decoration: none;
            font-size: 17px;
            text-transform: uppercase;
            color: white;
            font-family: monospace;
        }

        .sidnav-item:hover {
            text-decoration: none;
            color: #f5821f;
        }

        .sidnav-item:active {
            text-decoration: none;
            color: #f5821f;
        }

        .glyphicon-search,
        .glyphicon-user {
            margin-right: 20px;
        }

        .container {
            padding-left: 190px;
            padding-top: 40px;
        }

        .container__header {
            margin-bottom: 30px;
        }

        .form-group .btn-group {
            width: 100% !important;
        }

        .form-group .btn-group .multiselect {
            width: 100% !important;
        }

        .required {
            color: red;
        }

    </style>
</head>

<body>
<div class="navigation">
    <span class="navigation-brand">T!ckTrack</span>

    <div class="navigation-action-button">
        <a href="/search"> <span class="glyphicon glyphicon-search"></span></a>
        <a href="/personalInfo"><span class="glyphicon glyphicon-user"></span></a>
        <a href="/logout"><span class="glyphicon glyphicon-log-out"></span></a>
    </div>
</div>
<div class="sidnav">
    <div class="sidnav-item-conteiner">
        <a href="/regUserMain" class="sidnav-item">HOME</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/myTickets/1/30" class="sidnav-item">MY TICKETS</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/newTicket" class="sidnav-item">NEW TICKET</a>
    </div>
    <c:if test="${admin}">
        <div class="sidnav-item-conteiner">
            <a href="/admin/userManagement/1/30" class="sidnav-item">USER MANAGEMENT</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="/admin/categoryManagement" class="sidnav-item">CATEGORY MANAGEMENT</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="/admin/groupManagement" class="sidnav-item">GROUP MANAGEMENT</a>
        </div>
    </c:if>
</div>

<div class="container" align="left">
    <h4 align="left" class="container__header">Create a new ticket</h4>


    <form method="post" action="createTicket" id="create_form">
        <label>Summary</label>
        <span class="required">*</span>
        <div class="form-group">
            <textarea type="text" class="form-control" name="summary"
                      placeholder="Summary" required></textarea>
        </div>
        <label>Description</label>
        <span class="required">*</span>
        <div class="form-group">
            <textarea type="text" class="form-control" name="description" rows="6"
                      placeholder="Description" required></textarea>
        </div>
        <label>Assignee</label>
        <div class="form-group"
             oninput="document.getElementById('group').disabled=false">
            <div class="ui-widget">
                <input type="text" class="form-control" name="assignee"
                       placeholder="Assignee" id="assignee">
            </div>
        </div>

        <label>Group</label>
        <div class="form-group">
            <select id="group" name="group" class="form-control"
                    oninput="document.getElementById('assignee').disabled=false">
                <option selected disabled>Group</option>
                <c:forEach var="group" items="${groupList}">
                    <option value="${group}">
                            ${group}
                    </option>
                </c:forEach>
            </select>
        </div>

        <label>Priority</label>
        <span class="required">*</span>
        <div class="form-group">
            <select name="priority" name="priority[]" class="form-control" required>
                <option value="Low">Low</option>
                <option value="Medium">Medium</option>
                <option value="High">High</option>
                <option value="Critical">Critical</option>
            </select>
        </div>

        <label>Category</label>
        <span class="required">*</span>
        <div class="form-group">
            <select name="category" name="category[]" class="form-control" required>
                <c:forEach var="category" items="${categoryList}">
                    <option value="${category}">
                            ${category}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Deadline</label>
            <div class="input-daterange input-group" name="deadline">
                <input type="text" class="input-sm form-control" name="deadline"/>
            </div>
        </div>
        <div class="form-group">
            <input type="submit" class="btn btn-info" name="submit" value="Create"/>
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
            buttonWidth: '568px'
        });
        $('#group').multiselect({
            nonSelectedText: 'More',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '568px'
        });
    });

    $(document).ready(function () {
        $('#status').multiselect({
            nonSelectedText: 'Status',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '568px'
        });
    });
    $(document).ready(function () {
        $('#category').multiselect({
            nonSelectedText: 'Category',
            enableFiltering: true,
            enableCaseInsensitiveFiltering: true,
            includeSelectAllOption: true,
            buttonWidth: '568px'
        });
    });
    $(document).ready(function () {
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
            delay: 500,
            async: true,
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