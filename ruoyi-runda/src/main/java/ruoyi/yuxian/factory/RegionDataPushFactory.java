package ruoyi.yuxian.factory;

import ruoyi.yuxian.service.RegionDataPushService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RegionDataPushFactory {
    private final Map<String, RegionDataPushService> pushServiceMap;

    public RegionDataPushFactory(List<RegionDataPushService> pushServices) {
        this.pushServiceMap = pushServices.stream()
                .collect(Collectors.toMap(
                        RegionDataPushService::getSupportedRegionCode,
                        service -> service
                ));
    }

    public RegionDataPushService getService(String regionCode) {
        return pushServiceMap.get(regionCode);
    }
}