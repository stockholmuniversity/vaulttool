            <div class="card bottom-margin-medium">
                <div class="card-header">
                    <h3>Search Result</h3>
                </div>

                <div class="card-body top-padding-none bottom-padding-none cardBodyItemsListPadding">
                    <g:each in="${metadatas}" var="metadata" status="i">
                        <div class="row pointer">
                            <div class="col-sm-12 ${(i < metadatas.size() - 1) ? 'itemListBorder':''} right-padding-none left-padding-none bottom-padding-none top-padding-none">
                                <g:link class="secretsListLink" data-key="${metadata.secretKey}">
                                    <g:if test="${metadata.secretKey.contains('dummykeydontuse')}">
                                        <strong>${metadata.secretKey.substring(0, metadata.secretKey.indexOf('dummykeydontuse'))}</strong> <br />
                                    </g:if>
                                    <g:else>
                                        <strong>${metadata.secretKey}</strong> <br />
                                    </g:else>
                                     ${metadata?.title?:""}
                                </g:link>
                            </div>
                        </div>
                    </g:each>
                </div>
            </div>
            <div class="bottom-margin-large bottom-margin-small">
                <button id="cancelBtn" class="btn btn-default pull-right" name="Cancel" value="Cancel">Cancel</button>
            </div>
            <div class="clearfix"></div>
            <div>&nbsp;</div>
