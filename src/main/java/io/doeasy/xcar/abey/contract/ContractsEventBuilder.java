package io.doeasy.xcar.abey.contract;

import io.doeasy.xcar.abey.EventBuilder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;

import java.util.Arrays;

/**
 * @author kris.wang
 */
public class ContractsEventBuilder implements EventBuilder<ContractsEventEnum> {

    @Override
    public Event build(ContractsEventEnum type) {
        switch (type) {
            case STAKING:
                return stakingEvent();
            case WITHDRAW:
                return withdrawEvent();
            case EMERGENCIES_WITHDRAW:
                return emergenciesWithdrawEvent();
            case GET_REWARDS:
                return getRewardEvent();
            case NFT_TRANSFER:
                return getNftTransferEvent();
            case MINT:
                return getMintEvent();
            case ADD_REFERRER:
                return getAddReferrerEvent();
            case UPGRADE:
                return getUpgradeEvent();
            case ACTIVATION_REFERRAL:
                return getActivationReferralEvent();
            default:
                return null;
        }
    }

    private Event getUpgradeEvent() {
        Event event = new Event("Upgrade",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(false) {//user address
                        },
                        new TypeReference<Uint256>(true) { //tokenId
                        },
                        new TypeReference<Uint256>(false) { //targetLevel
                        },
                        new TypeReference<Uint256>(false) { //targetSpeed
                        }
                ));
        return event;
    }

    private Event getAddReferrerEvent() {
        Event event = new Event("AddReferral",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        }
                ));
        return event;
    }

    private Event getActivationReferralEvent() {
        Event event = new Event("ActivationReferral",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) { // referal
                        },
                        new TypeReference<Address>(true) { // referrer
                        }
                ));
        return event;
    }

    private Event stakingEvent() {
        Event event = new Event("Staking",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(false) {
                        },
                        new TypeReference<Uint8>(false) {
                        },
                        new TypeReference<DynamicArray<Uint256>>(false){
                        }
                ));
        return event;
    }

    private Event withdrawEvent() {
        Event event = new Event("Withdraw",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(false) {
                        },
                        new TypeReference<Uint8>(false) {
                        },
                        new TypeReference<DynamicArray<Uint256>>(false){
                        }
                ));
        return event;
    }

    private Event emergenciesWithdrawEvent() {
        Event event = new Event("EmergenciesWithdraw",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint8>(true) {
                        },
                        new TypeReference<DynamicArray<Uint256>>(){
                        }
                ));
        return event;
    }

    private Event getRewardEvent() {
        Event event = new Event("GetReward",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint8>(true) {
                        },
                        new TypeReference<DynamicArray<Uint256>>(){
                        },
                        new TypeReference<Uint256>(){
                        }
                ));
        return event;
    }

    private Event getNftTransferEvent() {
        Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint256>(true){
                        }
                ));
        return event;
    }

    private Event getMintEvent() {
        Event event = new Event("Mint",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(false) {
                        },
                        new TypeReference<Uint256>(false) {
                        },
                        new TypeReference<Uint8>(false){
                        },
                        new TypeReference<Uint8>(false){
                        },
                        new TypeReference<Uint256>(true){
                        }
                ));
        return event;
    }
}
