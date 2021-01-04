package com.qdt.blockchain.chain;

import java.util.ArrayList;
import java.util.List;

import com.qdt.blockchain.block.Block;

public class Chain {
	private List<Block> blockchain;

	public List<Block> getBlockChain() {
		if (blockchain == null)
			return new ArrayList<>();
		else
			return blockchain;
	}

	public boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;

		try {
			// loop through blockchain to check hashes:
			for (int i = 1; i < blockchain.size(); i++) {

				currentBlock = blockchain.get(i);
				previousBlock = blockchain.get(i - 1);
				// compare registered hash and calculated hash:
				if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
					System.out.println("#Current Hashes not equal");
					return false;
				}
				// compare previous hash and registered previous hash
				if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
					System.out.println("#Previous Hashes not equal");
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void addBlock(Block newBlock) {
		blockchain.add(newBlock);
	}
}
