import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AccountDetailsRenderService } from "../service/account-details-render-service";
import { AlertService } from "../service/alert-service";

const alertService = new AlertService();
const accountDetailsRestClient = new AccountDetailsRestClient();
const accountDetailsRenderService = new AccountDetailsRenderService(alertService, accountDetailsRestClient);

accountDetailsRenderService.init();
