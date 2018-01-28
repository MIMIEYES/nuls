/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.consensus.constant;

import io.nuls.core.chain.entity.Na;

/**
 * @author Niels
 */
public interface PocConsensusConstant {
    //version
    short POC_CONSENSUS_MODULE_VERSION = 0;
    short MINIMUM_VERSION_SUPPORTED = 0;


    String CFG_CONSENSUS_SECTION = "consensus";
    String PROPERTY_PARTAKE_PACKING = "partake.packing";
    String PROPERTY_SEED_NODES = "seed.nodes";
    String SEED_NODES_DELIMITER = ",";

    String GENESIS_BLOCK_FILE = "genesis-block.json";

    short EVENT_TYPE_GET_BLOCKS_HASH = 20;
    short EVENT_TYPE_BLOCKS_HASH = 21;

    /**
     *   THE PARAMETERS OF CONSENSUS,bellow
     */
    int CONFIRM_BLOCK_COUNT = 6;
    int MIN_CONSENSUS_AGENT_COUNT = 30;
    /**
     * Set temporarily as a fixed value,unit:nuls
     */
    int BLOCK_COUNT_OF_YEAR = 3153600;
    /**
     * value = 5000000/3154600
     */
    double BLOCK_REWARD = 1.5855;
    Na TRANSACTION_FEE = Na.CENT;
    /**
     * unit:second
     */
    int BLOCK_TIME_INTERVAL = 10;

    /**
     * default:2M
     */
    long MAX_BLOCK_SIZE = 2 << 21;

    Na AGENT_DEPOSIT_LOWER_LIMIT = Na.parseNuls(20000);
    Na ENTRUSTER_DEPOSIT_LOWER_LIMIT = Na.parseNuls(2000);
    /**
     * Maximum acceptable number of delegate
     */
    int MAX_ACCEPT_NUM_OF_DELEGATE = 1000;

    Na SUM_OF_DEPOSIT_OF_AGENT_LOWER_LIMIT = Na.parseNuls(200000);
    Na SUM_OF_DEPOSIT_OF_AGENT_UPPER_LIMIT = Na.parseNuls(500000);
    /**
     * Annual inflation
     */
    Na ANNUAL_INFLATION = Na.parseNuls(5000000);
    /**
     * unit: %
     */
    double AGENT_FORCED_EXITED_RATE = 70;
    /**
     * commission rate,UNIT:%
     */
    double MAX_COMMISSION_RATE = 20;
    double MIN_COMMISSION_RATE = 0;
    /**
     * unit:day
     */
    long RED_PUNISH_DEPOSIT_LOCKED_TIME = 90;
    long YELLOW_PUNISH_DEPOSIT_LOCKED_TIME = 3;

    /**
     * credit parameters
     */
    /**
     * unit:round of consensus
     */
    int RANGE_OF_CAPACITY_COEFFICIENT = 100;
    /**
     * Penalty coefficient,greater than 4.
     */
    int CREDIT_MAGIC_NUM = 4;
}
