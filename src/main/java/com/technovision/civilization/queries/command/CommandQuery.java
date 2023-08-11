package com.technovision.civilization.queries.command;

import com.technovision.civilization.data.objects.Resident;

public interface CommandQuery {
    void processResponse(String param);
    void processResponse(String response, Resident responder);
}
