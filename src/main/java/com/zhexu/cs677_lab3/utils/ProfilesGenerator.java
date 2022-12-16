package com.zhexu.cs677_lab3.utils;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab3.api.bean.basic.Address;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.config.InitConfigForRole;
import com.zhexu.cs677_lab3.api.bean.config.basic.ProfilesBean;
import com.zhexu.cs677_lab3.api.bean.freemarker.DockerComposeFileModel;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.*;

import static com.zhexu.cs677_lab3.constants.Consts.*;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
@Log4j2
public class ProfilesGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        YamlReader reader = new YamlReader(new FileReader(args[0]));
        ProfilesBean profilesBean = reader.read(ProfilesBean.class);

        if (profilesBean.involvedRoleNum()) {
            System.out.println("Seller or buyer number can not larger than peer number!");
            return;
        }

        Integer sleepBeforeStart = profilesBean.getSleepBeforeStart();
        Map<UUID, Address> tradersUuidAddressMap = createUUIDAddressMap(
                ZERO,
                profilesBean.getTraderNumber(),
                profilesBean.getPort(),
                profilesBean.getDeployOnSingleComputer());


        Map<UUID, Address> sellerAndBuyerAddressMap = createUUIDAddressMap(
                profilesBean.getTraderNumber(),
                profilesBean.getBuyerNumber() + profilesBean.getSellerNumber(),
                profilesBean.getPort() + profilesBean.getTraderNumber() + ONE,
                profilesBean.getDeployOnSingleComputer());

        List<InitConfigForRole> traders = createNeighbours(tradersUuidAddressMap);
        List<InitConfigForRole> sellerAndBuyers = new LinkedList<>() {{
            sellerAndBuyerAddressMap.forEach((k, v) -> {
                InitConfigForRole role = new InitConfigForRole();
                role.setId(k.toString());
                role.setSelfAdd(v);
                role.setNeighbours(new HashMap<>() {{
                    traders.forEach((trader) -> {
                        put(trader.getId(), trader.getSelfAdd());
                    });
                }});
                add(role);
            });
        }};

        List<InitConfigForRole> readyList = addProductAndStock(
                traders,
                sellerAndBuyers,
                profilesBean.getProductNameList(),
                profilesBean.getMaxmumStok(),
                profilesBean.getBuyerNumber(),
                profilesBean.getSellerNumber());

        generateProfiles(
                readyList,
                profilesBean.getDeployOnSingleComputer(),
                sleepBeforeStart,
                profilesBean.getMaxmumStok(),
                profilesBean.getNumberOfTests(),
                profilesBean.getRpcBuffSize(),
                profilesBean.getRunMode());
    }

    private static List<InitConfigForRole> addProductAndStock(List<InitConfigForRole> traderList,
                                                              List<InitConfigForRole> sellerBuyerList,
                                                              List<String> productNameList,
                                                              Integer maxmumStok,
                                                              Integer byerNum,
                                                              Integer sellerNum) {
        Random ra = new Random();
        Integer productListSize = productNameList.size();

        Map<Integer, Product> productMap = new HashMap<>() {{
            for (int i = 0; i < productListSize; i++) {
                put(i, new Product(i, productNameList.get(i)));
            }
        }};

        for (int i = 0; i < byerNum; i++) {
            log.info(sellerBuyerList.get(i).getSelfAdd().getDomain() +
                    "is assigned as buyer now");
            sellerBuyerList.get(i).becomeBuyer();
        }

        Map<String, Stock> stockMap = new HashMap<>();
        Map<Product, Integer> stockItem = new HashMap<>() {{
            for (Product product : productMap.values()) {
                put(product, ZERO);
            }
        }};

        for (int i = byerNum; i < byerNum + sellerNum; i++) {
            InitConfigForRole role = sellerBuyerList.get(i);

            role.becomeSeller();

            Stock stockObj = new Stock();
            stockObj.setSellerId(role.getId());
            stockObj.setStock(stockItem);
            stockMap.put(role.getId(), stockObj);

            log.info(role.getSelfAdd().getDomain() + "is assigned as seller now");
        }

        traderList.forEach((e) -> {
            e.setProducts(productMap);
            e.setStock(stockMap);
        });

        sellerBuyerList.forEach((e) -> {
            e.setProducts(productMap);
        });

        traderList.addAll(sellerBuyerList);

        return traderList;
    }


    private static List<InitConfigForRole> createNeighbours(Map<UUID, Address> uuidAddressMap) {
        Random ra = new Random();
        Integer peerNum = uuidAddressMap.size();
        List<InitConfigForRole> roleList = new LinkedList<>();

        uuidAddressMap.forEach((k, v) -> {
            InitConfigForRole role = new InitConfigForRole();
            role.setId(k.toString());
            role.setSelfAdd(v);
            role.setNeighbours(new HashMap<>());
            roleList.add(role);
        });

        for (int i = 0; i < peerNum; i++) {
            for (int j = i + 1; j < peerNum; j++) {
                roleList.get(i).putNeighbours(
                        roleList.get(j).getId(),
                        roleList.get(j).getSelfAdd()
                );

                roleList.get(j).putNeighbours(
                        roleList.get(i).getId(),
                        roleList.get(i).getSelfAdd()
                );

            }
        }

        return roleList;
    }

    private static Map<UUID, Address> createUUIDAddressMap(
            int indexStart,
            int peerNumber,
            Integer port,
            Boolean deployOnSingleComputer) {
        Map<UUID, Address> uuidAddressMap = new HashMap<>() {{
            for (int i = indexStart; i < indexStart + peerNumber; i++) {
                Address address = new Address();
                address.setDomain(DOMAIN_PREFIX + i + DOMAIN_SUFIX);
                address.setPort(deployOnSingleComputer ? port + i : port);
                put(UUID.randomUUID(), address);
            }
        }};

        return uuidAddressMap;
    }

    private static void generateProfiles(List<InitConfigForRole> readyList,
                                         Boolean deployOnSingleComputer,
                                         Integer sleepBeforeStart,
                                         Integer maxStock,
                                         Integer numberOfTests,
                                         Integer rpcBuffSize,
                                         Integer runMode) throws IOException, TemplateException {

        StringBuffer hostSB = new StringBuffer();
        StringBuffer argsSB = new StringBuffer();

        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(FREE_MARKER_TEMPLATE_DIR));

        Map<String, Object> rootForDockerFile = new HashMap<String, Object>();
        List<DockerComposeFileModel> dockerComposeFileModelList = new ArrayList<DockerComposeFileModel>();

        ObjectMapper mapper = new ObjectMapper();

        readyList.forEach((role) -> {
            role.setMaxStock(maxStock);
            String jsonDir = role.getSelfAdd().getDomain() + SLASH + JSON_PROFILE_DIR_BASE;
            File dir = new File(jsonDir);
            dir.mkdirs();
            String jsonFileName = jsonDir + SLASH + JSON_INIT_FILE_NAME;
            File jsonFile = new File(jsonFileName);
            try {
                mapper.writeValue(jsonFile, role);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            hostSB.append(LOCALHOST_IP + SPACE + role.getSelfAdd().getDomain() + ENTER);
            String argString = sleepBeforeStart +
                    SPACE +
                    numberOfTests +
                    SPACE +
                    rpcBuffSize +
                    SPACE +
                    maxStock +
                    SPACE +
                    runMode +
                    SPACE +
                    SERVER_PORT_ARG +
                    role.getSelfAdd().getPort() * 2;
            if (deployOnSingleComputer) {
                argsSB.append(argString + ENTER + ENTER);
            } else {
                String jsonFileInDocker = CURRENT_DIR +
                        JSON_PROFILE_DIR_BASE +
                        SLASH +
                        JSON_INIT_FILE_NAME;
                String runString = EXPORT_ENV_PREFIX +
                        jsonFileInDocker +
                        ENTER +
                        RUN_CMD +
                        SPACE +
                        argString;

                String runFileName = role.getSelfAdd().getDomain() + SLASH + RUN_BASH_FILE;
                BufferedWriter outRun = null;
                try {
                    outRun = new BufferedWriter(new FileWriter(runFileName));
                    outRun.write(runString);
                    outRun.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                DockerComposeFileModel dockerComposeFileModel = new DockerComposeFileModel();
                dockerComposeFileModel.setServiceName(role.getSelfAdd().getDomain());
                dockerComposeFileModel.setWorkingDir(CURRENT_DIR + role.getSelfAdd().getDomain());
                dockerComposeFileModelList.add(dockerComposeFileModel);

            }
        });

        if (deployOnSingleComputer) {
            String hostsFile = HOSTS_FILE;
            BufferedWriter outHost = new BufferedWriter(new FileWriter(hostsFile));
            outHost.write(hostSB.toString());
            outHost.close();

            String argsFile = ARGS_FILE;
            BufferedWriter outArg = new BufferedWriter(new FileWriter(argsFile));
            outArg.write(argsSB.toString());
            outArg.close();
        } else {
            rootForDockerFile.put(MODEL_LIST, dockerComposeFileModelList);

            Writer fileWriter = new FileWriter(new File(DOCKER_COMPOSE_FILE));
            try {
                Template template = cfg.getTemplate(FREE_MARKER_TEMPLATE_FILE_NAME);
                template.process(rootForDockerFile, fileWriter);
            } finally {
                fileWriter.close();
            }
        }
    }

}
