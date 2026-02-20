export enum VideoEvent{
    OFFER_RECEIVE = "receive_offer",
    OFFER_SEND = "send_offer",
    ICE_CANDIDATE_RECEIVE = "receive_ice_candidate",
    ICE_CANDIDATE_SEND = "send_ice_candidate",
    CLOSE_VIDEO = "close_video",
    OPEN_VIDEO = "open_video",
}