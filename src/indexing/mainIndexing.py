class mainIndexing:

    # structures: 
        # lexicon (to store the vocabulary terms and their information) 
        # document table (to store docid to docno mapping and document lengths)

    # compile flags:
        # ASCII format or binary format during debugging and for performance, respectively
        # enalbe/disble stemming & stopword removal

    def main(self):
    
        # uncompress documents 
        # (load one compressed file into memory and then call the right library function to uncompress it into another memory-based buffer)

        # for each document
            # split pid from text
            # call text processing 
            # create index
                # search new terms and add them
                # add a document to the posting list of a term

        # index compression (do not use standard compressors such as gzip for index compression)

        pass
                