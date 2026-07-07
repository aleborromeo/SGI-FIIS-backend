package com.sgi.fiis.resolutions.domain.port.in;

import com.sgi.fiis.resolutions.domain.model.Resolution;

public interface IssueResolutionUseCase {
    Resolution issue(IssueResolutionCommand command);
}
